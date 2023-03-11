package com.example.semestralka_pokus.gps_locator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.semestralka_pokus.R;
import com.example.semestralka_pokus.WelcomeActivity;
import com.example.semestralka_pokus.network.thread.LocatorThread;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocatorActivity extends AppCompatActivity {

    LocatorThread thread;

    private TextView label, longLabel, longValue, latLabel, latValue, timeLabel, timeValue;

    private Button searchButton, disconnectButton, homeButton, mapButton, stopButton;
    private double lon, lat;
    private LocalDateTime localDateTime;

    /**
     * Vytvori a zobrazi aktivitu.
     */
    @SuppressLint({"SetTextI18n", "UnspecifiedImmutableFlag", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        label = findViewById(R.id.label);
        longLabel = findViewById(R.id.tv_long_label);
        longValue = findViewById(R.id.tv_long);
        latLabel = findViewById(R.id.tv_lat_label);
        latValue = findViewById(R.id.tv_lat);
        timeLabel = findViewById(R.id.tv_timeLabel);
        timeValue = findViewById(R.id.tv_time);
        searchButton = findViewById(R.id.button_search);
        searchButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        disconnectButton = findViewById(R.id.button_disconnect);
        setClickabiltyDisconnectButton(false);
        homeButton = findViewById(R.id.button_home);
        homeButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        mapButton = findViewById(R.id.button_map);
        mapButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        stopButton = findViewById(R.id.button_stop);
        stopButton.setBackgroundColor(Color.argb(255, 118, 118, 255));

        label.setText("Posledni poloha");
        longLabel.setText("Zemepisna delka");
        latLabel.setText("Zemepisna sirka");
        timeLabel.setText("Datum a cas");
        longValue.setText(String.valueOf(0.0));
        latValue.setText(String.valueOf(0.0));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timeValue.setText(String.valueOf(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
        }

        thread = new LocatorThread(this);

        searchButton.setOnClickListener(view -> {
            thread.run();
            setClickabiltyDisconnectButton(false);
            setClickabiltySearchButton(false);
            Log.d("THREAD_START", "Vlakno pro lokator bylo spusteno.");
        });

        disconnectButton.setOnClickListener(view -> {
            Log.d("SCAN", "Koncim.");
            setClickabiltyDisconnectButton(false);
            setClickabiltySearchButton(true);
            Log.d("THREAD_CANCEL", "Vlakno pro lokator bylo ukonceno.");
        });

        homeButton.setOnClickListener(view -> {
            openHomeActivity();
        });

        stopButton.setOnClickListener(view -> {
            thread.cancel();
            setClickabiltySearchButton(true);
            Log.d("THREAD_CANCEL", "Vlakno pro lokator bylo ukonceno stop buttonem.");
        });

        mapButton.setOnClickListener(view -> {
            openMapActivity();
            Log.d("SETTINGS", "Jdu do nastaveni.");
        });
    }

    public void openHomeActivity() {
        Intent intent = new Intent(LocatorActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapLocationActivity.class);
        Bundle b = new Bundle();
        b.putDouble("lat", 0);
        b.putDouble("lon", 0);
        startActivity(intent);
        finish();
    }

    public void setClickabiltySearchButton(boolean clickabilty) {
        if (!clickabilty)
            searchButton.setBackgroundColor(Color.argb(255, 118, 118, 188));
        else
            searchButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        searchButton.setClickable(clickabilty);
    }

    public void setClickabiltyDisconnectButton(boolean clickabilty) {
        if (!clickabilty)
            disconnectButton.setBackgroundColor(Color.argb(255, 118, 118, 188));
        else
            disconnectButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        disconnectButton.setClickable(clickabilty);
    }

    public void setLocation(float longitude, float latitude) {
        lon = longitude;
        lat = latitude;
        longValue.setText(String.valueOf(lon));
        latValue.setText(String.valueOf(lat));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timeValue.setText(String.valueOf(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));
        }
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}