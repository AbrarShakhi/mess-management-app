package com.github.abrarshakhi.mmap.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.abrarshakhi.mmap.R;

public class MealsFragment extends Fragment {
    public MealsFragment() {
    }

    @NonNull
    public static MealsFragment newInstance() {
        //        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new MealsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meals, container, false);
    }
}