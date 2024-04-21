package com.example.myapplication5;

import android.util.Log;
import java.io.BufferedReader;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import java.io.OutputStream;
import java.io.InputStreamReader;

import com.example.myapplication5.ui.dashboard.DashboardViewModel;
import com.example.myapplication5.ui.home.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import com.example.myapplication5.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private HomeViewModel homeViewModel;
    private DashboardViewModel dashboardViewModel;

    String[] mandatoryKeys = {"average_connectivity_time_per_operator", "average_connectivity_time_per_network_type", "average_signal_power_per_network_type", "average_snr_per_network_type", "average_signal_power_per_device"};

    TextView statisticsText;

    Button button;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private final Runnable sendDataRunnable = new Runnable() {
        @Override
        public void run() {
            String ipAddress = "10.0.2.2";
            int port = 8080;
            String dataToSend = formatData();

            try {
                Socket socket = new Socket(ipAddress, port);
                socket.setSoTimeout(500);
                Log.i("SocketTask", "connected to socket");
                OutputStream outputStream = socket.getOutputStream();
                Log.i("SocketTask", "Data to send: " + dataToSend);
                outputStream.write(dataToSend.getBytes());
                outputStream.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receivedData = reader.readLine();
                Log.d("SocketTask", "Json data: " + receivedData);

                JSONObject response = parseAndValidateJson(receivedData, mandatoryKeys);
                displayData(response);

                reader.close();
                socket.close();
            } catch (IOException e) {
                Log.e("Insertion Error", "Error inserting cell info: " + e.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkPermission()) {
            requestPermission();
        }

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
            }
        };
        TelephonyManager.CellInfoCallback callback = null;
        callback = new TelephonyManager.CellInfoCallback() {
            @Override
            public void onCellInfo(@NonNull List<CellInfo> cellInfo) {
            }
        };
        telephonyManager.requestCellInfoUpdate(executor, callback);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        statisticsText = findViewById(R.id.statisticsText);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        //Schedule the task to run every 10 sec
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(
                sendDataRunnable,
                0,
                10,
                TimeUnit.SECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                PERMISSION_REQUEST_CODE);
    }

    private String formatData() {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("signal_power", homeViewModel.getSignalStrength().getValue());
            jsonData.put("snr", homeViewModel.getSnr().getValue());
            jsonData.put("frequency_band", homeViewModel.getFrequency().getValue());
            jsonData.put("Time", homeViewModel.getTime().getValue());
            jsonData.put("cell_id", homeViewModel.getCellId().getValue());
            jsonData.put("network_type", homeViewModel.getNetworkType().getValue());
            jsonData.put("operator", homeViewModel.getNetworkOperator().getValue());
            jsonData.put("date_1", dashboardViewModel.getStartDate().getValue());
            jsonData.put("date_2", dashboardViewModel.getEndDate().getValue());
        } catch (JSONException e) {
            Log.e("FormatData",  e.getMessage());
        }
        return jsonData.toString();
    }

    private JSONObject parseAndValidateJson(String jsonString, String... mandatoryKeys) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonString);

            // Validate mandatory keys
            for (String key : mandatoryKeys) {
                if (!jsonObject.has(key)) {
                    return null;
                }
            }
        } catch (JSONException e) {
            Log.e("parseAndValidateJson", e.getMessage());
        }

        return jsonObject;
    }

    private void displayData(JSONObject jsonObject) {
        if(jsonObject==null){
            return;
        }
        try {
            dashboardViewModel.postAvgConnOperator(prettyJson(jsonObject.getString("average_connectivity_time_per_operator")));
            dashboardViewModel.postAvgConnNetwork(prettyJson(jsonObject.getString("average_connectivity_time_per_network_type")));
            dashboardViewModel.postAvgSigPowType(prettyJson(jsonObject.getString("average_signal_power_per_network_type")));
            dashboardViewModel.postAvgSNRType(prettyJson(jsonObject.getString("average_snr_per_network_type")));
            dashboardViewModel.postAvgSigPowDevice(prettyJson(jsonObject.getString("average_signal_power_per_device")));
        } catch (JSONException e) {
            Log.e("FormatData", e.getMessage());
        }
    }

    private String prettyJson(String jsonString) {
        StringBuilder formattedString = new StringBuilder();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                formattedString.append(key).append(": ").append(value);
                if (keys.hasNext()) {
                    formattedString.append(", ");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return formattedString.toString();
    }

}