package com.github.abrarshakhi.mmap.mess.presentation;

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
import com.github.abrarshakhi.mmap.home.domain.model.Mess;

import java.util.List;

public class MessItemAdapter extends ArrayAdapter<Mess> {
    private final Context context;
    private final List<Mess> items;
    private final EditCreateMessViewModel viewModel;

    public MessItemAdapter(@NonNull Context context, @NonNull List<Mess> items, EditCreateMessViewModel viewModel) {
        super(context, R.layout.mess_item, items);
        this.context = context;
        this.items = items;
        this.viewModel = viewModel;
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

        Mess mess = items.get(position);

        holder.tvMessName.setText(mess.getName());
        holder.tvMessLocation.setText(mess.getLocation());
        holder.tvMessCity.setText(mess.getCity());
        holder.llIdItemCard.setOnClickListener(v -> viewModel.selectMess(mess));

        return convertView;
    }

    static class ViewHolder {
        LinearLayout llIdItemCard;
        TextView tvMessName;
        TextView tvMessLocation;
        TextView tvMessCity;
    }
}
