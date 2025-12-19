package com.github.abrarshakhi.mmap.home.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroceryItemAdapter extends ArrayAdapter<GroceryBatch> {

    private final Context context;
    private final List<GroceryBatch> items;

    public GroceryItemAdapter(@NonNull Context context, @NonNull List<GroceryBatch> items) {
        super(context, R.layout.list_grocery, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.list_grocery, parent, false);
        }

        GroceryBatch batch = items.get(position);

        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        TextView tvTimestamp = convertView.findViewById(R.id.tvTimestamp);
        TextView tvTotalPrice = convertView.findViewById(R.id.tvTotalPrice);
        Button btnView = convertView.findViewById(R.id.btnView);

        // Username
        tvUserName.setText(batch.userName != null ? batch.userName : "Unknown");

        // Timestamp (human readable)
        tvTimestamp.setText(formatTimestamp(batch.getTimestamp()));

        // Total price
        tvTotalPrice.setText("Total: ৳" + calculateTotal(batch));

        // Button click
        btnView.setOnClickListener(v -> {
            StringBuilder items = new StringBuilder();

            for (int i = 0; i < batch.getItemNames().length; i++) {
                items.append(batch.getItemNames()[i])
                        .append(" - ")
                        .append(batch.getQuantities()[i])
                        .append(" - ৳")
                        .append(batch.getPrices()[i])
                        .append("\n");
            }

            new AlertDialog.Builder(context)
                    .setTitle("Grocery Details")
                    .setMessage(items.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });

        return convertView;
    }

    private float calculateTotal(GroceryBatch batch) {
        float total = 0f;
        float[] prices = batch.getPrices();

        if (prices != null) {
            for (float price : prices) {
                total += price;
            }
        }
        return total;
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

}
