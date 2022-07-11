package com.example.semestralka_pokus.substance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.semestralka_pokus.R;

import java.util.ArrayList;

/**
 * Aktivita pro seznam pripravku na pole.
 */
public class SubstancesActivity extends AppCompatActivity {

    /* Obsah pole. */
    public double fieldArea;

    /**
     * Vytvori aktivitu.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substances);

        RecyclerView rvSubstances = findViewById(R.id.rv_substances);

        ArrayList<Substance> substances = new ArrayList<>();

        substances.add(new Substance("POLIFOSKA", 0.012));
        substances.add(new Substance("SUPERFOSFÁT", 0.0056));
        substances.add(new Substance("SÍRAN AMONNÝ", 0.0185));

        Bundle b = getIntent().getExtras();
        if(b != null) {
            fieldArea = b.getDouble("area");
        }

        SubstancesAdapter adapter = new SubstancesAdapter(substances, fieldArea);

        rvSubstances.setAdapter(adapter);
        rvSubstances.setLayoutManager(new LinearLayoutManager(this));
    }
}