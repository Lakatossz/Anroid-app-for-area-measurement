package com.example.semestralka_pokus.field;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semestralka_pokus.R;
import com.example.semestralka_pokus.field.fieldDB.FieldDBAdapter;
import com.example.semestralka_pokus.substance.SubstancesActivity;

import java.util.List;

/**
 * Adapter pro praci s RecyclerVirewerem.
 */
public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.FieldsViewHolder> {

    public static class FieldsViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_fieldName_label;
        public TextView tv_area_label;
        public TextView tv_perimeter_label;
        public TextView tv_fieldName_value;
        public TextView tv_area_value;
        public TextView tv_perimeter_value;
        public Button b_substance;
        public Button b_remove;

        public FieldsViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_fieldName_label = itemView.findViewById(R.id.tv_fieldName_label);
            tv_area_label = itemView.findViewById(R.id.tv_long_label);
            tv_perimeter_label = itemView.findViewById(R.id.tv_perim_label);
            tv_fieldName_value = itemView.findViewById(R.id.tv_fieldName_value);
            tv_area_value = itemView.findViewById(R.id.tv_area_value);
            tv_perimeter_value = itemView.findViewById(R.id.tv_perim_value);
            b_substance = itemView.findViewById(R.id.b_subs);
            b_remove = itemView.findViewById(R.id.b_remove);
        }
    }

    /* Uchovava ulozena pole. */
    private final List<Field> fieldList;

    /* Pro praci s databazi. */
    public FieldDBAdapter dbAdapter;

    public FieldsAdapter(Context context) {
        dbAdapter = new FieldDBAdapter(context);
        dbAdapter.open();
        this.fieldList = dbAdapter.fetchAllFields();
    }

    /**
     * Pripravi ViewHolder.
     */
    @NonNull
    @Override
    public FieldsAdapter.FieldsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View fieldView = inflater.inflate(R.layout.field_record, parent, false);
        return new FieldsViewHolder(fieldView);
    }

    /**
     * Nabinduje vsechny buttony a textviewy.
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull FieldsAdapter.FieldsViewHolder holder, int position) {
        Field field = fieldList.get(position);

        TextView tv_fieldName_label = holder.tv_fieldName_label;
        tv_fieldName_label.setText("Název: ");
        TextView tv_area_label = holder.tv_area_label;
        tv_area_label.setText("Obsah [m²]: ");
        TextView tv_perim_label = holder.tv_perimeter_label;
        tv_perim_label.setText("Obvod [m]: ");
        TextView tv_name_value = holder.tv_fieldName_value;
        tv_name_value.setText(field.getName());
        TextView tv_area_value = holder.tv_area_value;
        tv_area_value.setText(String.format("%.4f", field.getArea()));
        TextView tv_perim_value = holder.tv_perimeter_value;
        tv_perim_value.setText(String.format("%.4f", field.getPerimeter()));
        Button b_subs = holder.b_substance;
        b_subs.setText("YYBRAT");
        b_subs.setBackgroundColor(Color.GREEN);
        b_subs.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SubstancesActivity.class);
            Bundle b = new Bundle();
            b.putDouble("area", field.getArea());
            intent.putExtras(b);
            view.getContext().startActivity(intent);
        });
        Button b_remove = holder.b_remove;
        b_remove.setText("ODSTRANIT");
        b_remove.setBackgroundColor(Color.GREEN);
        b_remove.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(b_remove.getContext()).create();
            alertDialog.setTitle("Odstranit");
            alertDialog.setMessage("Přejete si pole odstranit?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ANO",
                    (dialog, which) ->  {
                        removeAt(tv_name_value.getText().toString(), holder.getAdapterPosition());
                        dialog.dismiss();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NE",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        });
    }

    /**
     * Vrati pocet radku v poli, predstavujici radky v recyclervieweru.
     */
    @Override
    public int getItemCount() {
        return fieldList.size();
    }

    /**
     * Odstrani radek z recyclervieweru i pole.
     */
    public void removeAt(String name, int position) {
        if(dbAdapter.deleteRow(name)) {
            fieldList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, fieldList.size());
        }
    }
}
