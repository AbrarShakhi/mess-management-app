package com.github.abrarshakhi.mmap.presentation.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.abrarshakhi.mmap.R;

public class ManagementFragment extends Fragment {

    public ManagementFragment() {
    }

    @NonNull
    public static ManagementFragment newInstance() {
        return new ManagementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_management, container, false);
    }
}