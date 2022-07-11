package com.example.semestralka_pokus.field;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.semestralka_pokus.R;

/**
 * Aktivita pro praci s databazi poli.
 */
public class FieldsActivity extends AppCompatActivity {

    /**
     * Vytvori a zobrazi aktivitu.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields);

        RecyclerView rvFields = findViewById(R.id.rv_fields);

        FieldsAdapter adapter = new FieldsAdapter(getApplicationContext());

        rvFields.setAdapter(adapter);
        rvFields.setLayoutManager(new LinearLayoutManager(this));
    }
}