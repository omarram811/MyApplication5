package com.example.myapplication5.ui.home;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication5.UIDataExtractor;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> mNetworkOperator = new MutableLiveData<>();
    private final MutableLiveData<String> mCellId = new MutableLiveData<>();
    private final MutableLiveData<String> mNetworkType = new MutableLiveData<>();
    private final MutableLiveData<String> mSignalStrength = new MutableLiveData<>();
    private final MutableLiveData<String> mSnr = new MutableLiveData<>();
    private final MutableLiveData<String> mTime = new MutableLiveData<>();
    private final MutableLiveData<String> mFrequency = new MutableLiveData<>();


    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public MutableLiveData<String> getNetworkOperator() {
        return mNetworkOperator;
    }
    public void setNetworkOperator(String networkOperator) {mNetworkOperator.setValue(networkOperator);}
    public MutableLiveData<String> getCellId() {
        return mCellId;
    }
    public void setCellId(String cellId) {mCellId.setValue(cellId);}
    public MutableLiveData<String> getNetworkType() {
        return mNetworkType;
    }
    public void setNetworkType(String networkType) {mNetworkType.setValue(networkType);}
    public MutableLiveData<String> getSignalStrength() {
        return mSignalStrength;
    }
    public void setSignalStrength(String signalStrength) {mSignalStrength.setValue(signalStrength);}
    public MutableLiveData<String> getSnr() {
        return mSnr;
    }
    public void setSnr(String snr) {mSnr.setValue(snr);}
    public MutableLiveData<String> getTime() {
        return mTime;
    }
    public void setTime(String time) {mTime.setValue(time);}
    public MutableLiveData<String> getFrequency() {
        return mFrequency;
    }
    public void setFrequency(String frequency) {
        mFrequency.setValue(frequency);
    }

    public void updateAll(Context context) {
        setNetworkOperator(UIDataExtractor.getOperator(context));
        setCellId(UIDataExtractor.getCellID(context));
        setNetworkType(UIDataExtractor.getNetworkType(context));
        setSignalStrength(UIDataExtractor.getSignalStrength(context));
        setSnr(UIDataExtractor.getSNR(context));
        setTime(UIDataExtractor.getDate(context));
        setFrequency(UIDataExtractor.getFrequency(context));
    }

    public LiveData<String> getText() {
        return mText;
    }

}