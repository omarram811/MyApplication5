package com.example.myapplication5;


import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.content.ContentValues;
import android.content.Context;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication5.R;
import com.example.myapplication5.databinding.FragmentHomeBinding;

import java.util.Objects;

public class UIDataExtractor {

    public static String getOperator(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        return Objects.requireNonNull(manager.getNetworkOperatorName());
    }

    public static String getCellID(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return "";
        }
        List<CellInfo> cellInfoList = manager.getAllCellInfo();
        if (cellInfoList == null) {
            return "";
        }
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                int CellId = cellIdentityGsm.getCid();
                return String.valueOf(CellId);
            } else if (cellInfo instanceof CellInfoWcdma) { //WCDMA = UMTS
                CellInfoWcdma cellInfoUmts = (CellInfoWcdma) cellInfo;
                CellIdentityWcdma cellIdentityUmts = cellInfoUmts.getCellIdentity();
                int CellId = cellIdentityUmts.getCid();
                return String.valueOf(CellId);
            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                int CellId = cellIdentityLte.getCi();
                return String.valueOf(CellId);
            }
        }
        return "";
    }

    public static String getNetworkType(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return getNetworkType(manager.getNetworkType());
    }

    private static String getNetworkType(int networkType) {
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

    public static String getSignalStrength(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return "";
        }
        List<CellInfo> cellInfoList = manager.getAllCellInfo();
        if (cellInfoList == null) {
            return "";
        }
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                int signalStrength = cellSignalStrengthGsm.getDbm();
                return signalStrength + "dBm";
            } else if (cellInfo instanceof CellInfoWcdma) { //WCDMA = UMTS
                CellInfoWcdma cellInfoUmts = (CellInfoWcdma) cellInfo;
                CellSignalStrengthWcdma cellSignalStrengthUmts = cellInfoUmts.getCellSignalStrength();
                int signalStrength = cellSignalStrengthUmts.getDbm();
                return signalStrength + "dBm";
            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                int signalStrength = cellSignalStrengthLte.getDbm();
                return signalStrength + "dBm";
            }
        }
        return "";
    }

    public static String getSNR(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        List<CellInfo> cellInfoList = manager.getAllCellInfo();
        if (cellInfoList == null) {
            return "";
        }
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) { //not applicable for GSM
                return "NONE";
            } else if (cellInfo instanceof CellInfoWcdma) { //WCDMA = UMTS
                return "NONE"; //not applicable
            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                return String.valueOf(cellSignalStrengthLte.getRssnr());
            }
        }
        return "";
    }

    public static String getDate(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        List<CellInfo> cellInfoList = manager.getAllCellInfo();
        if (cellInfoList == null) {
            return "";
        }
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        return date;
    }

    public static String getFrequency(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        List<CellInfo> cellInfoList = manager.getAllCellInfo();
        if (cellInfoList == null) {
            return "";
        }
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) { //not applicable for GSM
                return "NONE";
            } else if (cellInfo instanceof CellInfoWcdma) { //WCDMA = UMTS
                CellInfoWcdma cellInfoUmts = (CellInfoWcdma) cellInfo;
                CellIdentityWcdma cellIdentityUmts = cellInfoUmts.getCellIdentity();
                //UARFCN stands for UTRA Absolute Radio Frequency Channel Number
                //Frequency Band = UARFCNx0.2
                int UARFCN = cellIdentityUmts.getUarfcn();
                float frequencyBand = (float) (UARFCN * 0.2);
                return (String.valueOf(frequencyBand));
            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                int frequencyBand =  cellIdentityLte.getBandwidth();
                return (String.valueOf(frequencyBand));
            }
        }
        return "";
    }


    /*public static void insertCellInfo(String operator, String signalPower, String snr, String networkType, String frequencyBand, String cellId, String timestamp) {
        MainActivity mainActivity = new MainActivity();
        DatabaseHelper dbHelper = mainActivity.getDbHelper();
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
                Toast.makeText(mainActivity, "Error inserting cell info", Toast.LENGTH_SHORT).show();
                Log.e("Insertion Error", "Error inserting cell info");
            } else {
                Toast.makeText(mainActivity, "Cell info inserted with row ID: " + newRowId, Toast.LENGTH_SHORT).show();
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("Insertion Error", "Error inserting cell info: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }*/




}
