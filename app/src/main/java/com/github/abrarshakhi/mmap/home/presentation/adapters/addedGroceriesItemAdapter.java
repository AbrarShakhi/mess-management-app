package com.github.abrarshakhi.mmap.home.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.home.domain.model.AddedItem;

import java.util.List;

public class addedGroceriesItemAdapter extends ArrayAdapter<AddedItem> {

    private final Context context;
    private final List<AddedItem> items;

    public addedGroceriesItemAdapter(@NonNull Context context,@NonNull List<AddedItem> items) {
        super(context, R.layout.added_groceries_item, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.added_groceries_item, parent, false);
            holder = new ViewHolder();
            holder.tvItemName = convertView.findViewById(R.id.tvItemName);
            holder.tvItemPrice = convertView.findViewById(R.id.tvItemPrice);
            holder.tvItemQuantity = convertView.findViewById(R.id.tvItemQuantity);
            holder.btnDeleteItem = convertView.findViewById(R.id.btnDeleteItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AddedItem item = items.get(position);
        holder.tvItemName.setText(item.itemName);
        holder.tvItemPrice.setText(String.valueOf(item.price));
        holder.tvItemQuantity.setText(item.quantity);

        return convertView;
    }

    static class ViewHolder {
        TextView tvItemName;
        TextView tvItemPrice;
        TextView tvItemQuantity;
        ImageButton btnDeleteItem;
    }
}