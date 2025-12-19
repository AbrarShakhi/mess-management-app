package com.github.abrarshakhi.mmap.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.data.dto.MessDto;

import java.util.List;

public class MessItemAdapter extends ArrayAdapter<MessDto> {
    private final Context context;
    private final List<MessDto> items;
    private final OnMessClickListener clickListener;

    public interface OnMessClickListener {
        void onClick(MessDto mess);
    }

    public MessItemAdapter(@NonNull Context context, @NonNull List<MessDto> items, @NonNull OnMessClickListener listener) {
        super(context, R.layout.mess_item, items);
        this.context = context;
        this.items = items;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.mess_item, parent, false);

            holder = new ViewHolder();
            holder.tvMessName = convertView.findViewById(R.id.tvMessName);
            holder.tvMessLocation = convertView.findViewById(R.id.tvMessLocation);
            holder.tvMessCity = convertView.findViewById(R.id.tvMessCity);
            holder.llIdItemCard = convertView.findViewById(R.id.llIdItemCard);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MessDto mess = items.get(position);

        holder.tvMessName.setText(mess.name);
        holder.tvMessLocation.setText(mess.location);
        holder.tvMessCity.setText(mess.city);

        holder.llIdItemCard.setOnClickListener(v -> clickListener.onClick(mess));

        return convertView;
    }

    static class ViewHolder {
        LinearLayout llIdItemCard;
        TextView tvMessName;
        TextView tvMessLocation;
        TextView tvMessCity;
    }
}
