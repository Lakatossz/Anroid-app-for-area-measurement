package com.example.semestralka_pokus.gps;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.semestralka_pokus.measure.MeasureActivity;

public class GPSTracker extends Service implements LocationListener {

    /* Promenna pro pristup k ziskani lokace. */
    boolean isGPSEnabled = false;

    /* Promenna pro zjisteni moznosti ziskani lokace. */
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;
    Activity activity;

    /* Minimalni potrebna vzdalenost pro zmenu polohy. */
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 1 meter

    /* Minimalni casovy rozdil pro zmenu polohy. */
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 second

    protected LocationManager locationManager;

    public GPSTracker(Activity activity) {
        this.activity = activity;
        getLocation();
    }

    /**
     *  Metoda pro ziskani polohy.
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled) {
                this.canGetLocation = true;

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    Log.d("permition", "Permition not granted");
                    return null;
                }

                if (isGPSEnabled) {
                    if (location == null && locationManager != null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /*public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }*/

    /**
     * Vrati zemepisnou sirku.
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * Vrati zemepisnou delku.
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /*public boolean canGetLocation() {
        return this.canGetLocation;
    }*/

    /**
     * Je zavolana pri zmene polohy.
     */
    @Override
    public void onLocationChanged(Location location) {
        getLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}