package com.example.semestralka_pokus.gps_locator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.semestralka_pokus.network.BTNetwork;

public class GPSLocatorService extends BroadcastReceiver {

    private final BTNetwork network;

    public GPSLocatorService(LocatorActivity activity) {
        Context context = activity.getApplicationContext();
        network = new BTNetwork(activity);

        if (network.intialize()) {
            network.startScanning();
            Log.d("CANCEL_THREAD", "Jsem na konci.");
        }
        else
            Log.d("CANCEL_THREAD", "Neco se nepovedlo.");
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (network.intialize()) {
            network.startScanning();
            Log.d("CANCEL_THREAD", "Jsem na konci.");
        }
        else
            Log.d("CANCEL_THREAD", "Neco se nepovedlo.");
    }
}
