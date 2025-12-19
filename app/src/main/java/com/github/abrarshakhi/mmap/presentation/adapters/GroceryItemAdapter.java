package com.github.abrarshakhi.mmap.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.data.dto.AddedItemDto;
import com.github.abrarshakhi.mmap.domain.model.GroceryBatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroceryItemAdapter extends ArrayAdapter<GroceryBatch> {

    private final Context context;

    public GroceryItemAdapter(
        @NonNull Context context,
        @NonNull List<GroceryBatch> items
    ) {
        super(context, R.layout.list_grocery, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.list_grocery, parent, false);
        }

        GroceryBatch batch = getItem(position);
        if (batch == null) return convertView;

        TextView tvUserName = convertView.findViewById(R.id.tvUserName);
        TextView tvTimestamp = convertView.findViewById(R.id.tvTimestamp);
        TextView tvTotalPrice = convertView.findViewById(R.id.tvTotalPrice);

        tvUserName.setText(
            batch.userName != null ? batch.userName : "Unknown"
        );

        tvTimestamp.setText(formatTimestamp(batch.timestamp));

        tvTotalPrice.setText(String.format(
            Locale.US,
            "Total: %.2f %s",
            batch.getTotalPrice(),
            batch.currency
        ));

        convertView.findViewById(R.id.btnView)
            .setOnClickListener(v -> showDetails(batch));

        return convertView;
    }

    /* ---------------- helpers ---------------- */

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf =
            new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void showDetails(GroceryBatch batch) {

        if (batch.items == null || batch.items.isEmpty()) {
            new AlertDialog.Builder(context)
                .setMessage("No items")
                .setPositiveButton("OK", null)
                .show();
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (AddedItemDto item : batch.items) {
            builder.append(item.itemName)
                .append(" - ")
                .append(item.quantity)
                .append(" - ৳")
                .append(item.price)
                .append("\n");
        }

        new AlertDialog.Builder(context)
            .setTitle("Grocery Details")
            .setMessage(builder.toString())
            .setPositiveButton("OK", null)
            .show();
    }
}

