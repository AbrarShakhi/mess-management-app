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
import com.github.abrarshakhi.mmap.domain.model.MealTracking;

import java.util.ArrayList;

public class DailyMealAdapter extends ArrayAdapter<MealTracking> {

    private final LayoutInflater inflater;

    public DailyMealAdapter(@NonNull Context context) {
        super(context, R.layout.item_daily_meal, new ArrayList<>());
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_daily_meal, parent, false);
        }

        MealTracking item = getItem(position);
        if (item == null) return view;

        TextView tvDayMeal = view.findViewById(R.id.tvDayMeal);
        TextView tvTotalMeal = view.findViewById(R.id.tvTotalMeal);
        LinearLayout container = view.findViewById(R.id.containerMembers);

        tvDayMeal.setText(
            "Day " + item.day + " - " + item.mealTime
        );

        tvTotalMeal.setText(
            "Total: " + item.mealCount + " meals"
        );

        // Clear previous member rows
        container.removeAllViews();

        // Add member rows dynamically
        for (MealTracking.IndividualMeal im : item.individualMealList) {

            View row = inflater.inflate(
                R.layout.item_member_meal,
                container,
                false
            );

            TextView tvName = row.findViewById(R.id.tvMemberName);
            TextView tvMeal = row.findViewById(R.id.tvMemberMeal);

            tvName.setText(im.member.fullName);
            tvMeal.setText(String.valueOf(im.mealCount));

            container.addView(row);
        }

        return view;
    }
}
