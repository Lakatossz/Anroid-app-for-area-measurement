package com.example.semestralka_pokus.substance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semestralka_pokus.R;

import java.util.List;

/**
 * Adapter pro praci s recyclerviewerem.
 */
public class SubstancesAdapter extends RecyclerView.Adapter<SubstancesAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_substanceName_label;

        public TextView tv_destiny_label;

        public TextView tv_substanceName_value;

        public TextView tv_density_value;

        public Button b_choose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_substanceName_label = itemView.findViewById(R.id.tv_substanceName_label);
            tv_destiny_label = itemView.findViewById(R.id.tv_density_label);
            tv_substanceName_value = itemView.findViewById(R.id.tv_substanceName_value);
            tv_density_value = itemView.findViewById(R.id.tv_density_value);
            b_choose = itemView.findViewById(R.id.b_choose);
            b_choose.setBackgroundColor(Color.GREEN);
        }
    }

    /* Seznam pripravku. */
    private final List<Substance> substanceList;

    double fieldArea;

    public SubstancesAdapter(List<Substance> substances, double fieldArea) {
        substanceList = substances;
        this.fieldArea = fieldArea;
    }

    /**
     * Pripravi ViewHolder.
     */
    @NonNull
    @Override
    public SubstancesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View substanceView = inflater.inflate(R.layout.substance_record, parent, false);

        return new ViewHolder(substanceView);
    }

    /**
     * Nabinduje vsechny buttony a textviewy.
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull SubstancesAdapter.ViewHolder holder, int position) {
        Substance substance = substanceList.get(position);

        TextView tv_substanceName_label = holder.tv_substanceName_label;
        tv_substanceName_label.setText("Přípravek: ");
        TextView tv_destiny_label = holder.tv_destiny_label;
        tv_destiny_label.setText("Hustota [kg/m²]: ");
        TextView tv_substanceName_value = holder.tv_substanceName_value;
        tv_substanceName_value.setText(substance.getName());
        TextView tv_density_value = holder.tv_density_value;
        tv_density_value.setText(String.format("%.4f", substance.getDensity()));
        Button b_choose = holder.b_choose;
        b_choose.setText("VYBRAT");
        b_choose.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(b_choose.getContext()).create();
            alertDialog.setTitle("Výsledek");
            alertDialog.setMessage("Výsledek je " + String.format("%.4f", fieldArea * substance.getDensity()) + " kg");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        });
    }

    /**
     * Vrati pocet radku, zaroven velikost pole.
     * @return
     */
    @Override
    public int getItemCount() {
        return substanceList.size();
    }
}
