package com.example.screenmirror;

import android.app.Application;
import android.util.Log;

import com.example.screenmirror.Manager.AppOpenManager;
import com.example.screenmirror.utility.NetworkMonitoringUtil;

public class NetworkMonitoringApplication extends Application {
    public static final String TAG = NetworkMonitoringApplication.class.getSimpleName();

    public NetworkMonitoringUtil mNetworkMonitoringUtil;
    private static AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");

        // this is continious monitoring connection
        mNetworkMonitoringUtil = new NetworkMonitoringUtil(getApplicationContext());
        mNetworkMonitoringUtil.checkNetworkState();
        mNetworkMonitoringUtil.registerNetworkCallbackEvents();


        appOpenManager = new AppOpenManager(this);

    }
}
