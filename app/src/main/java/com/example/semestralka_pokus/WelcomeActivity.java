package com.example.semestralka_pokus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.semestralka_pokus.field.FieldsActivity;
import com.example.semestralka_pokus.gps_locator.LocatorActivity;
import com.example.semestralka_pokus.measure.MeasureActivity;

/**
 * Uvodni aktivita aplikace.
 */
public class WelcomeActivity extends AppCompatActivity {

    public Button newButton, fieldsButton, locatorButton;

    /**
     * Zobrazi uvodni aktivitu aplikace.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newButton = findViewById(R.id.newButton);
        newButton.setOnClickListener(v -> openMeasureActivity());
        newButton.setBackgroundColor(Color.GREEN);
        newButton.setText("NOVÝ");

        fieldsButton = findViewById(R.id.fieldsButton);
        fieldsButton.setOnClickListener(v -> openFieldsActivity());
        fieldsButton.setBackgroundColor(Color.GREEN);

        locatorButton = findViewById(R.id.locatorButton);
        locatorButton.setOnClickListener(v -> openLocatorActivity());
        locatorButton.setBackgroundColor(Color.GREEN);

        if (ActivityCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }

    /**
     * Vyvola aktivitu pro mereni.
     */
    public void openMeasureActivity() {
        Intent intent = new Intent(this, MeasureActivity.class);
        startActivity(intent);
    }

    /**
     * Vyvola aktivitu pro seznam zmerenych poli.
     */
    public void openFieldsActivity() {
        Intent intent = new Intent(this, FieldsActivity.class);
        startActivity(intent);
    }

    public void openLocatorActivity() {
        Intent intent = new Intent(this, LocatorActivity.class);
        startActivity(intent);
    }
}