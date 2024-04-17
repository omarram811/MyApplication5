package com.example.myapplication5;

import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStream;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

import java.io.OutputStream;
import java.io.InputStreamReader;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Handler;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.myapplication5.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    TextView statisticsText;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private final Handler handler = new Handler();
    private final Runnable sendDataRunnable = new Runnable() {
        @Override
        public void run() {
            sendDataToServer();
            handler.postDelayed(this, 10000); // Send data every 10 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        statisticsText = findViewById(R.id.statisticsText);


        //Button refreshButton = findViewById(R.id.refreshButton);


        handler.post(sendDataRunnable);
        sendDataToServer();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(sendDataRunnable);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static class SocketClientThread extends Thread {
        private final String serverIp;
        private final int serverPort;
        private final String data;
        private final Handler handler;
        private final TextView statisticsText;

        public SocketClientThread(String serverIp, int serverPort, String data, Handler handler, TextView statisticsText) {
            this.serverIp = serverIp;
            this.serverPort = serverPort;
            this.data = data;
            this.handler = handler;
            this.statisticsText = statisticsText;
        }

        @Override
        public void run() {
            // Your socket client logic here
            try {
                Socket socket = new Socket(serverIp, serverPort);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();

                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder receivedData = new StringBuilder();
                String line;
                while ((line=reader.readLine()) != null){
                    receivedData.append(line);
                }
                handler.post(() -> {
                    statisticsText.setText(receivedData.toString());
                });
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDataToServer(){
        String serverIp = "10.169.25.20";
        int serverPort = 1337;
        String data = formatData();
        Handler handler = new Handler(Looper.getMainLooper());
        statisticsText = findViewById(R.id.statisticsText);
        new SocketClientThread(serverIp, serverPort, data, handler, statisticsText).start();
    }
    private String formatData() {
        JSONObject jsonData = new JSONObject();
        TextView signalStrengthText = findViewById(R.id.signalStrengthText);
        TextView snrText = findViewById(R.id.snrText);
        TextView frequencyText = findViewById(R.id.frequencyText);
        TextView timeText = findViewById(R.id.timeText);
        TextView cellIdText = findViewById(R.id.cellIdText);
        TextView networkTypeText = findViewById(R.id.networkTypeText);
        TextView networkOperatorText = findViewById(R.id.networkOperatorText);
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