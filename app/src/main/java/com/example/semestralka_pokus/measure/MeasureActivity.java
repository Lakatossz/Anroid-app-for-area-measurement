package com.example.semestralka_pokus.measure;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.semestralka_pokus.gps.GPSTracker;
import com.example.semestralka_pokus.R;
import com.example.semestralka_pokus.WelcomeActivity;
import com.example.semestralka_pokus.field.Field;
import com.example.semestralka_pokus.field.FieldsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Aktivita pro mereni pole.
 */
public class MeasureActivity extends AppCompatActivity {

    public TextView tv_area, tv_areaLabel, tv_count, tv_countLabel, tv_perim, tv_perimLabel;
    public Button addButton, countButton, reloadButton, homeButton;
    public double area = 0, perimeter = 0;

    /* Vektor namerenych hodnot. */
    List<Coordinate> coordinateList = new ArrayList<>();

    /* Instance pro mereni polohy. */
    GPSTracker gpsTracker;

    /**
     * Vytvori a zobrazi aktivitu.
     */
    @SuppressLint({"SetTextI18n", "UnspecifiedImmutableFlag", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measure);

//        gpsTracker = new GPSTracker(MeasureActivity.this);

        tv_areaLabel = findViewById(R.id.tv_area_label);
        tv_area = findViewById(R.id.tv_area);
        tv_perimLabel = findViewById(R.id.tv_perim_label);
        tv_perim = findViewById(R.id.tv_perim);
        tv_countLabel = findViewById(R.id.tv_countLabel);
        tv_count = findViewById(R.id.tv_count);
        addButton = findViewById(R.id.b_add);
        addButton.setText("PŘIDAT");
        countButton = findViewById(R.id.b_count);
        countButton.setText("SPOČÍTAT");
        homeButton = findViewById(R.id.b_backhome);
        reloadButton = findViewById(R.id.b_reload);
        homeButton.setText("ZPĚT");
        addButton.setBackgroundColor(Color.GREEN);
        countButton.setBackgroundColor(Color.GREEN);
        homeButton.setBackgroundColor(Color.GREEN);
        reloadButton.setBackgroundColor(Color.GREEN);

        tv_areaLabel.setText("Obsah [m²]: ");
        tv_perimLabel.setText("Obvod [m]: ");
        tv_countLabel.setText("Počet: ");
        tv_area.setText(String.valueOf(0));
        tv_perim.setText(String.valueOf(0));
        tv_count.setText(String.valueOf(0));

        addButton.setOnClickListener(view -> addCoordinates());

        countButton.setOnClickListener(view -> {
            if(coordinateList.size() > 2) {
                area = CalculatePolygonArea();
                perimeter = CalculatePerimeter();
                tv_perim.setText(String.format("%.4f", perimeter));
                tv_area.setText(String.format("%.4f", area));
                tv_count.setText(String.valueOf(coordinateList.size()));
                AlertDialog alertDialog = new AlertDialog.Builder(MeasureActivity.this).create();
                alertDialog.setTitle("Přidat do seznamu");
                alertDialog.setMessage("Chcete přidat změřené pole do seznamu? (Vložte název)");

                final EditText et_fieldName = new EditText(MeasureActivity.this);

                alertDialog.setView(et_fieldName);

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) ->  {
                            FieldsAdapter adapter = new FieldsAdapter(MeasureActivity.this);
                            adapter.dbAdapter.open();
                            String FieldName = et_fieldName.getText().toString();
                            adapter.dbAdapter.createRow(new Field(FieldName, area, perimeter));
                            dialog.dismiss();
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ZRUŠIT",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "Malo bodu...", Toast.LENGTH_SHORT). show();
            }
        });

        homeButton.setOnClickListener(view -> {
            coordinateList.clear();
            Intent intent = new Intent(MeasureActivity.this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        reloadButton.setOnClickListener(view -> {
            coordinateList.clear();
            tv_area.setText(String.valueOf(0));
            tv_perim.setText(String.valueOf(0));
            tv_count.setText(String.valueOf(0));
        });
    }

    /**
     * Zmeri a prida polohu do pole.
     */
    private void addCoordinates() {
        gpsTracker = new GPSTracker(MeasureActivity.this);

        coordinateList.add(new Coordinate(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
        tv_count.setText(String.valueOf(coordinateList.size()));

        Log.d("measure", "Aktualni pozice: " + gpsTracker.getLatitude() + ", " + gpsTracker.getLongitude());
        Toast.makeText(this, "Změna pozice: " + gpsTracker.getLatitude() + ", " + gpsTracker.getLongitude(), Toast.LENGTH_LONG).show();
        gpsTracker = null;
    }

    /**
     * Vypocita obsah pozemku, jehoz tvar je urceny polem bodu poloh.
     */
    private double CalculatePolygonArea() {
        double area = 0;

        Vector<Coordinate> forCalc = new Vector<>(coordinateList);
        forCalc.add(coordinateList.get(0));

        if (forCalc.size() > 2) {
            for (int i = 0; i < forCalc.size() - 1; i++) {
                area += ConvertToRadian(forCalc.elementAt(i + 1).getLongitude() - forCalc.elementAt(i).getLongitude()) * (2 + Math.sin(ConvertToRadian(forCalc.elementAt(i).getLatitude())) + Math.sin(ConvertToRadian(forCalc.elementAt(i + 1).getLatitude())));
            }
            area = area * 6378137 * 6378137 / 2;
        }
        Log.d("area", String.valueOf(Math.abs(area)));
        return Math.abs(area);
    }

    /**
     * Prevede uhel ze stupnu na radiany.
     */
    private double ConvertToRadian(double input)
    {
        return input * Math.PI / 180;
    }

    /**
     * Vypocita obvod zmereneho pole.
     */
    private double CalculatePerimeter() {
        double perimeter = 0;

        Vector<Coordinate> forCalc = new Vector<>(coordinateList);
        forCalc.add(coordinateList.get(0));

        if (forCalc.size() > 2) {
            for (int i = 0; i < forCalc.size() - 1; i++) {
                perimeter += DistanceInMeters(forCalc.elementAt(i).getLatitude(), forCalc.elementAt(i + 1).getLatitude(), forCalc.elementAt(i).getLongitude(), forCalc.elementAt(i + 1).getLongitude());
            }
        }

        return perimeter;
    }

    /**
     * Vypocita vzdalenost mezi dvema body ulozene jako souradnice.
     */
    private double DistanceInMeters(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }
}