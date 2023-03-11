package com.example.semestralka_pokus.gps_locator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.semestralka_pokus.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private double lat = 0, lon = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            lat = b.getDouble("lat");
            lon = b.getDouble("lon");
        }

        TextView longValue = findViewById(R.id.longitude);
        TextView latValue = findViewById(R.id.lattitude);
        TextView longLabel = findViewById(R.id.tv_long_label);
        TextView latLabel = findViewById(R.id.tv_lat_label);
        longValue.setText(String.valueOf(lon));
        latValue.setText(String.valueOf(lat));
        longLabel.setText("Zemepisna delka");
        latLabel.setText("Zemepisna sirka");

        Button backButton = findViewById(R.id.back_button);
        backButton.setBackgroundColor(Color.argb(255, 118, 118, 255));
        backButton.setOnClickListener(view -> {
            openLocatorActivity();
            Log.d("THREAD_CANCEL", "Vlakno pro lokator bylo ukonceno stop buttonem.");
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng locator = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions()
                .position(locator)
                .title("Pozice lokátoru"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locator, 2));
    }

    public void openLocatorActivity() {
        Intent intent = new Intent(MapLocationActivity.this, LocatorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}