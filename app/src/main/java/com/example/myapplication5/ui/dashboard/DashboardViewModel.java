package com.example.myapplication5.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mStartDate = new MutableLiveData<>();
    private final MutableLiveData<String> mEndDate = new MutableLiveData<>();
    private final MutableLiveData<String> mAvgConnOperator = new MutableLiveData<>();
    private final MutableLiveData<String> mAvgConnNetwork = new MutableLiveData<>();
    private final MutableLiveData<String> mAvgSigPowType = new MutableLiveData<>();
    private final MutableLiveData<String> mAvgSigPowDevice = new MutableLiveData<>();
    private final MutableLiveData<String> mAvgSNRType = new MutableLiveData<>();

    private final MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public MutableLiveData<String> getStartDate() { return mStartDate; }
    public MutableLiveData<String> getEndDate() { return mEndDate; }
    public LiveData<String> getAvgConnOperator() { return mAvgConnOperator; }
    public void postAvgConnOperator(String avgConnOperator) { mAvgConnOperator.postValue(avgConnOperator); }
    public LiveData<String> getAvgConnNetwork() { return mAvgConnNetwork; }
    public void postAvgConnNetwork(String avgConnNetwork) { mAvgConnNetwork.postValue(avgConnNetwork); }
    public LiveData<String> getAvgSigPowType() { return mAvgSigPowType; }
    public void postAvgSigPowType(String avgSigPowType) { mAvgSigPowType.postValue(avgSigPowType); }
    public LiveData<String> getAvgSigPowDevice() { return mAvgSigPowDevice; }
    public void postAvgSigPowDevice(String avgSigPowDevice) { mAvgSigPowDevice.postValue(avgSigPowDevice); }
    public LiveData<String> getAvgSNRType() { return mAvgSNRType; }
    public void postAvgSNRType(String avgSNRType) { mAvgSNRType.postValue(avgSNRType); }
}