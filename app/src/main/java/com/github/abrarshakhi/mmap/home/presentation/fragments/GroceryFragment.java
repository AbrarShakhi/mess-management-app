package com.github.abrarshakhi.mmap.home.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.abrarshakhi.mmap.R;


public class GroceryFragment extends Fragment {
    protected GroceryFragment() {
    }

    @NonNull
    public static GroceryFragment newInstance() {
        // Bundle args = new Bundle();
        // fragment.setArguments(args);
        return new GroceryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grocery, container, false);
    }
}