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
import java.util.concurrent.Executor;
import java.io.DataOutputStream;

import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.IOException;

import com.example.myapplication5.ui.home.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
    //MyThread myThread;


    private final Handler handler = new Handler();
    private final Runnable sendDataRunnable = new Runnable() {
        @Override
        public void run() {
            queryCellInfo();
            sendDataToServer();
            handler.postDelayed(this, 10000); // Send data every 10 seconds
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        SocketViewModel socketViewModel =
                new ViewModelProvider(this).get(SocketViewModel.class);

        // Connect to the server
        socketViewModel.startListening("192.168.100.154", 1337);

        dbHelper = new DatabaseHelper(this);

        statisticsText = findViewById(R.id.statisticsText);

        queryCellInfo();

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RefreshButton", "Refresh button clicked");
                // Call the method to refresh cell info data
                refreshCellularInfo();
            }
        });

        handler.post(sendDataRunnable);
        sendDataToServer();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(sendDataRunnable);

    }

    // Method to provide access to dbHelper
    //private DatabaseHelper getDbHelper() {
        //return dbHelper;
    //}



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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                queryCellInfo();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkMobileNet(Context context) {//Checks if device is connected to a mobile network
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        }
        return false;
    }

    public static boolean checkWifi(Context context) {//Checks if the device is connected to a WiFi network
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        }
        return false;
    }

    public String getOperator() {
        TelephonyManager manager = (TelephonyManager) getSystemService(TelephonyManager.class);
        return Objects.requireNonNull(manager.getNetworkOperatorName());
    }

    public String getNetworkType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "GSM";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "New Radio (5G)";
            default:
                return "Outside Scope";
        }

    }

    protected void queryCellInfo() {
        // Check if permissions are granted
        if (checkPermission()) {
            // Permissions are granted, proceed with querying cell info
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            int networkType = telephonyManager.getNetworkType();
            networkTypeText.setText(getNetworkType(networkType));
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
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            //Log.d("List of Values", String.valueOf(cellInfoList));
            if (cellInfoList != null) {
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                timeText.setText(date);
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                        int signalStrength = cellSignalStrengthGsm.getDbm();
                        signalStrengthText.setText(signalStrength + "dBm");

                        CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                        int CellId = cellIdentityGsm.getCid();
                        cellIdText.setText(String.valueOf(CellId));


                        //not applicable for GSM
                        snrText.setText(String.valueOf("NONE"));
                        frequencyText.setText(String.valueOf("NONE"));
                        insertCellInfo(networkOperatorText.getText().toString(), signalStrengthText.getText().toString(), snrText.getText().toString(),networkTypeText.getText().toString(), frequencyText.getText().toString(), cellIdText.getText().toString(), timeText.getText().toString());

                        //updateCellInfo();

                    } else if (cellInfo instanceof CellInfoWcdma) { //WCDMA = UMTS
                        CellInfoWcdma cellInfoUmts = (CellInfoWcdma) cellInfo;
                        CellSignalStrengthWcdma cellSignalStrengthUmts = cellInfoUmts.getCellSignalStrength();
                        int signalStrength = cellSignalStrengthUmts.getDbm();
                        signalStrengthText.setText(signalStrength + "dBm");


                        CellIdentityWcdma cellIdentityUmts = cellInfoUmts.getCellIdentity();
                        int CellId = cellIdentityUmts.getCid();
                        cellIdText.setText(String.valueOf(CellId));


                        //UARFCN stands for UTRA Absolute Radio Frequency Channel Number
                        //Frequency Band = UARFCNx0.2
                        int UARFCN = cellIdentityUmts.getUarfcn();
                        float frequencyBand = (float) (UARFCN * 0.2);
                        frequencyText.setText(String.valueOf(frequencyBand));


                        //not available
                        snrText.setText(String.valueOf("None"));

                        insertCellInfo(networkOperatorText.getText().toString(), signalStrengthText.getText().toString(), snrText.getText().toString(),networkTypeText.getText().toString(), frequencyText.getText().toString(), cellIdText.getText().toString(), timeText.getText().toString());

                    } else if(cellInfo instanceof CellInfoLte){
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;

                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        int signalStrength = cellSignalStrengthLte.getDbm();
                        signalStrengthText.setText(signalStrength + "dBm");

                        CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                        int CellId = cellIdentityLte.getCi();
                        cellIdText.setText(String.valueOf(CellId));

                        int SNR = cellSignalStrengthLte.getRssnr();
                        snrText.setText(String.valueOf(SNR));

                        int frequencyBand =  cellIdentityLte.getBandwidth();
                        frequencyText.setText(String.valueOf(frequencyBand));

                        insertCellInfo(networkOperatorText.getText().toString(), signalStrengthText.getText().toString(), snrText.getText().toString(),networkTypeText.getText().toString(), frequencyText.getText().toString(), cellIdText.getText().toString(), timeText.getText().toString());
                    }
                }
            }
        } else {
            // Permissions are not granted, request them
            requestPermission();
        }
    }

    public void refreshCellularInfo() {
        // Clear existing text values to indicate refreshing
        Log.d("RefreshCellularInfo", "Refreshing cellular info");
        signalStrengthText.setText("");
        snrText.setText("");
        frequencyText.setText("");
        timeText.setText("");
        cellIdText.setText("");
        networkTypeText.setText("");
        networkOperatorText.setText(getOperator());
        // Your existing code to query cell info
        queryCellInfo();
        Log.d("RefreshCellularInfo", "Cell info refreshed: " + signalStrengthText.getText().toString());
    }


    private void insertCellInfo(String operator, String signalPower, String snr, String networkType, String frequencyBand, String cellId, String timestamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try{
            ContentValues values = new ContentValues();
            values.put(CellInfoContract.CellInfoEntry.COLUMN_NAME_OPERATOR, operator);
            values.put(CellInfoContract.CellInfoEntry.COLUMN_NAME_SIGNAL_POWER, signalPower);
            values.put(CellInfoContract.CellInfoEntry.COLUMN_NAME_SNR, snr);
            values.put(CellInfoContract.CellInfoEntry.COLUMN_NAME_NETWORK_TYPE, networkType);
            values.put(CellInfoContract.CellInfoEntry.COLUMN_NAME_FREQUENCY_BAND, frequencyBand);
            values.put(CellInfoContract.CellInfoEntry.COLUMN_NAME_CELL_ID, cellId);
            values.put(CellInfoContract.CellInfoEntry.COLUMN_NAME_TIMESTAMP, timestamp);

            long newRowId = db.insert(CellInfoContract.CellInfoEntry.TABLE_NAME, null, values);
            if (newRowId == -1) {
                Toast.makeText(this, "Error inserting cell info", Toast.LENGTH_SHORT).show();
                Log.e("Insertion Error", "Error inserting cell info");
            } else {
                Toast.makeText(this, "Cell info inserted with row ID: " + newRowId, Toast.LENGTH_SHORT).show();
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("Insertion Error", "Error inserting cell info: " + e.getMessage());
        } finally {
            db.endTransaction();
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
                    Log.d("Received Lines", line);
                }
                handler.post(() -> {
                    statisticsText.setText(receivedData.toString());
                });
                Log.d("SnipsChips", receivedData.toString()); //Doesn't log until server closes
                socket.close();
            } catch (IOException e) {
                Log.d("Reached Exception!!!!!", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void sendDataToServer(){
        String serverIp = "192.168.100.154";
        int serverPort = 1337;
        String data = formatData();
        Handler handler = new Handler(Looper.getMainLooper());
        statisticsText = findViewById(R.id.statisticsText);
        //new SocketClientThread(serverIp, serverPort, data, handler, statisticsText).start();

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