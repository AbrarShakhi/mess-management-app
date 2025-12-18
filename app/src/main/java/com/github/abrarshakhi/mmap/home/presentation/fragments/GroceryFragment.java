package com.github.abrarshakhi.mmap.home.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.abrarshakhi.mmap.databinding.FragmentGroceryBinding;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.data.repository.GroceryRepositoryImpl;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;
import com.github.abrarshakhi.mmap.home.domain.usecase.FindCurrentMonthYearUseCase;
import com.github.abrarshakhi.mmap.home.domain.usecase.ListGroceryBatchUseCase;
import com.github.abrarshakhi.mmap.home.presentation.activities.GroceryManageActivity;
import com.github.abrarshakhi.mmap.home.presentation.viewmodel.GroceryViewModel;
import com.github.abrarshakhi.mmap.home.presentation.viewmodel.ProfileViewModel;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class GroceryFragment extends Fragment {
    public FragmentGroceryBinding binding;
    public GroceryViewModel viewModel;
    public List<GroceryBatch> groceryBatchList;

    public GroceryFragment() {
    }

    @NonNull
    @Contract(" -> new")
    public static GroceryFragment newInstance() {
        return new GroceryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (!modelClass.isAssignableFrom(ProfileViewModel.class)) {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
                var dataSource = new DataSource(requireContext());
                var GroceryRepository = new GroceryRepositoryImpl(dataSource);
                var listGroceryBatchUseCase = new ListGroceryBatchUseCase(GroceryRepository);
                var findCurrentMonthYearUseCase = new FindCurrentMonthYearUseCase(GroceryRepository);
                return (T) new GroceryViewModel(listGroceryBatchUseCase, findCurrentMonthYearUseCase);
            }
        }).get(GroceryViewModel.class);
        groceryBatchList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroceryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.totalPrice.observe(getViewLifecycleOwner(), price -> {
            if (price != null) {
                binding.tvTotSpend.setText(String.format(Locale.US, "%.2f", price));
            }
        });
        viewModel.monthYear.observe(getViewLifecycleOwner(), outcome -> {
            if (outcome.isOK()) {
                viewModel.listGroceries();
                binding.tvMonthYear.setText(outcome.unwrap().toString());
            } else {
                Toast.makeText(requireContext(), outcome.unwrapErr(), Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.listGroceries.observe(getViewLifecycleOwner(), outcome -> {
            if (outcome.isOK()) {
                groceryBatchList.clear();
                groceryBatchList.addAll(outcome.unwrap());
                // TODO: NOTIFY ADAPTER;
            }
        });

        viewModel.findMonthYear();
        viewModel.listGroceries();

        binding.btnAddGrocery.setOnClickListener(v -> startActivity(new Intent(requireActivity(), GroceryManageActivity.class)));
        binding.ivArrowLeft.setOnClickListener(v -> viewModel.previousMonth());
        binding.ivArrowRight.setOnClickListener(v -> viewModel.nextMonth());
    }
}