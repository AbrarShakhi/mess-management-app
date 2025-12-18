package com.github.abrarshakhi.mmap.home.presentation.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.home.domain.model.AddedItem;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;

import java.util.List;

public class GroceryItemAdapter extends ArrayAdapter<GroceryBatch> {

    private final Context context;
    private final List<GroceryBatch> items;

    public GroceryItemAdapter(@NonNull Context context, @NonNull List<GroceryBatch> items) {
        super(context, , items);
        this.context = context;
        this.items = items;
    }

}
