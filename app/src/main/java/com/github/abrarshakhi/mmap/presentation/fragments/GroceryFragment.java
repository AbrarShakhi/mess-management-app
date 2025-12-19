package com.github.abrarshakhi.mmap.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.GroceryBatchDto;
import com.github.abrarshakhi.mmap.data.mapper.GroceryMapper;
import com.github.abrarshakhi.mmap.databinding.FragmentGroceryBinding;
import com.github.abrarshakhi.mmap.domain.model.GroceryBatch;
import com.github.abrarshakhi.mmap.domain.model.MonthYear;
import com.github.abrarshakhi.mmap.presentation.activities.GroceryManageActivity;
import com.github.abrarshakhi.mmap.presentation.adapters.GroceryItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroceryFragment extends Fragment {

    private final List<GroceryBatch> list = new ArrayList<>();
    private final Map<String, String> userNameMap = new HashMap<>();
    private FragmentGroceryBinding binding;
    private HomeDataSource dataSource;
    private GroceryItemAdapter adapter;
    private String messCurrency = "BDT";
    private MonthYear monthYear;

    public GroceryFragment() {
    }

    @NonNull
    public static GroceryFragment newInstance() {
        return new GroceryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new HomeDataSource(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroceryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        adapter = new GroceryItemAdapter(requireContext(), list);
        binding.lvGroceryItem.setAdapter(adapter);
        fetchMonthYearThenLoad();

        binding.btnAddGrocery.setOnClickListener(v ->
            startActivity(new Intent(requireActivity(), GroceryManageActivity.class)));

        binding.ivArrowLeft.setOnClickListener(v -> {
            monthYear = monthYear.previous();
            loadGroceries();
        });

        binding.ivArrowRight.setOnClickListener(v -> {
            monthYear = monthYear.next();
            loadGroceries();
        });
    }

    private void loadGroceries() {
        String messId = dataSource.getCurrentMessId();

        dataSource.getGroceries(
            messId,
            monthYear.getMonth(), monthYear.getYear(),
            this::onGroceriesLoaded,
            e -> Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void fetchMonthYearThenLoad() {
        String messId = dataSource.getCurrentMessId();

        dataSource.getCurrentMonthYearFromMess(
            messId,
            my -> {
                monthYear = my;
                binding.tvMonthYear.setText(monthYear.toString());
                prepareCaches();
            },
            e -> Toast.makeText(
                requireContext(),
                e.getMessage(),
                Toast.LENGTH_SHORT
            ).show()
        );
    }


    private void onGroceriesLoaded(List<GroceryBatchDto> dtos) {
        list.clear();
        float total = 0f;

        for (GroceryBatchDto dto : dtos) {
            GroceryBatch batch = GroceryMapper.toUi(dto, userNameMap.getOrDefault(dto.userId, "Unknown"), messCurrency);
            list.add(batch);
            total += batch.getTotalPrice();
        }
        binding.tvMonthYear.setText(monthYear.toString());
        binding.tvTotSpend.setText(String.valueOf(total));
        adapter.notifyDataSetChanged();
    }

    private void prepareCaches() {
        String messId = dataSource.getCurrentMessId();

        dataSource.getMess(messId, mess -> {
            messCurrency = mess.currency;
            if (mess.members != null) {
                for (var member : mess.members) {
                    dataSource.fetchUserProfile(member.userId, user -> {
                        userNameMap.put(member.userId, user.fullName);
                    }, e -> {
                        userNameMap.put(member.userId, "Unknown");
                    });
                }
            }
            loadGroceries();
        }, e -> {
        });
    }

}
