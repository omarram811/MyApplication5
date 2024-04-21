package com.example.myapplication5;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintWriter;
import android.os.Bundle;
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.io.DataOutputStream;

import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Handler;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.io.OutputStream;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;


import com.example.myapplication5.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    TextView signalStrengthText;
    TextView snrText;
    TextView frequencyText;
    TextView timeText;
    TextView cellIdText;
    TextView networkTypeText;
    TextView networkOperatorText;
    TextView statisticsText;

    Button button;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    protected DatabaseHelper dbHelper;


    private final Runnable sendDataRunnable = new Runnable() {
        @Override
        public void run() {
            String ipAddress = "10.0.2.2";
            int port = 8080;
            String dataToSend = formatData();

            try {
                Socket socket = new Socket(ipAddress, port);
                Log.i("SocketTask", "connected to socket");
                OutputStream outputStream = socket.getOutputStream();
                Log.i("SocketTask", "Data to send: " + dataToSend);
                outputStream.write(dataToSend.getBytes());
                outputStream.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String receivedData = reader.readLine();

                Log.d("SocketTask", "Json data: " + receivedData);

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

        signalStrengthText = findViewById(R.id.signalStrengthText);
        snrText = findViewById(R.id.snrText);
        frequencyText = findViewById(R.id.frequencyText);
        timeText = findViewById(R.id.timeText);
        cellIdText = findViewById(R.id.cellIdText);
        networkTypeText = findViewById(R.id.networkTypeText);
        networkOperatorText = findViewById(R.id.networkOperatorText);
        networkOperatorText.setText(getOperator());
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

        dbHelper = new DatabaseHelper(this);

        statisticsText = findViewById(R.id.statisticsText);

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
        //handler.removeCallbacks(sendDataRunnable);

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

    public String getOperator() {
        TelephonyManager manager = (TelephonyManager) getSystemService(TelephonyManager.class);
        return Objects.requireNonNull(manager.getNetworkOperatorName());
    }

    private String formatData() {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("signal_power", signalStrengthText.getText());
            jsonData.put("snr", snrText.getText());
            jsonData.put("frequency_band", frequencyText.getText());
            jsonData.put("Time", timeText.getText());
            jsonData.put("cell_id", cellIdText.getText());
            jsonData.put("network_type", networkTypeText.getText());
            jsonData.put("operator", networkOperatorText.getText());
            jsonData.put("date_1", "2024-04-14 13:30:00");
            jsonData.put("date_2", "2024-04-16 11:00:00");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonData.toString();
    }
}