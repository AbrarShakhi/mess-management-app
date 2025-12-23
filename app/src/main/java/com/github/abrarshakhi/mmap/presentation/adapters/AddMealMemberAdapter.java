package com.github.abrarshakhi.mmap.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.domain.model.MessMember;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMealMemberAdapter extends ArrayAdapter<MessMember> {

    private final LayoutInflater inflater;
    private final Map<String, Integer> mealCounts = new HashMap<>();

    public AddMealMemberAdapter(Context context, List<MessMember> members) {
        super(context, 0, members);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_add_meal_member, parent, false);
        }

        MessMember member = getItem(position);
        if (member == null) return view;

        TextView tvName = view.findViewById(R.id.tvName);
        Spinner spinner = view.findViewById(R.id.spinnerCount);

        tvName.setText(member.fullName);

        ArrayAdapter<Integer> spinnerAdapter =
            new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Arrays.asList(0, 1, 2, 3, 4, 5)
            );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                mealCounts.put(member.userId, (Integer) parent.getItemAtPosition(pos));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        return view;
    }

    public Map<String, Integer> getMealCounts() {
        return mealCounts;
    }
}
