package com.example.semestralka_pokus.network.thread;

import android.content.Context;
import android.util.Log;

import com.example.semestralka_pokus.gps_locator.LocatorActivity;
import com.example.semestralka_pokus.network.BTNetwork;

public class LocatorThread extends Thread {

    private BTNetwork network;
    private Context context;

    public LocatorThread(LocatorActivity activity) {
        this.context = activity.getApplicationContext();
        network = new BTNetwork(activity);
    }

    public void run() {
        if (network.intialize()) {
            network.startScanning();
            Log.d("CANCEL_THREAD", "Jsem na konci.");
        }
        else
            Log.d("CANCEL_THREAD", "Neco se nepovedlo.");
    }

    public void cancel() {
        network.disconnectDevice();
        Log.d("CANCEL_THREAD", "Odpojuji se od zarizeni.");
    }
}
