package com.example.semestralka_pokus.network.thread;

import android.util.Log;

import com.example.semestralka_pokus.gps_locator.LocatorActivity;
import com.example.semestralka_pokus.network.BTNetwork;

public class LocatorThread extends Thread {

    private BTNetwork network;

    public LocatorThread(LocatorActivity activity) {
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
