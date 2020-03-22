package com.example.uploadapp.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.uploadapp.utils.CommonUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static MutableLiveData<Boolean> networkChange = new MutableLiveData<>();

    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        networkChange.postValue(CommonUtils.getConnectivityStatus(context));
    }

}
