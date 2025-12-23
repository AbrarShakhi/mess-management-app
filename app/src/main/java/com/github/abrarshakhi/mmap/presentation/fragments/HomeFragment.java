package com.github.abrarshakhi.mmap.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.GroceryBatchDto;
import com.github.abrarshakhi.mmap.data.dto.MealTrackingDto;
import com.github.abrarshakhi.mmap.data.dto.PaymentDto;
import com.github.abrarshakhi.mmap.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeDataSource dataSource;
    private String currentUserId;
    private String messId;
    private int month, year;
    // Variables to store intermediate results
    private float totalGrocery = 0;
    private float userGrocery = 0;
    private int totalMealCount = 0;
    private int userMealCount = 0;
    private float totalPaymentsByUser = 0;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new HomeDataSource(requireContext());

        FirebaseUser user = dataSource.getLoggedInUser();
        if (user != null) currentUserId = user.getUid();

        messId = dataSource.getCurrentMessId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (messId == null || messId.isEmpty()) return;

        loadAllStats();
    }

    private void loadAllStats() {
        // 1️⃣ Fetch mess info
        dataSource.getMess(messId, mess -> {
            month = mess.month;
            year = mess.year;

            // Fetch groceries, meals, and payments in parallel
            fetchGroceries();
            fetchMeals();
            fetchPayments();

        }, e -> {
            // Handle error
        });
    }

    private void fetchGroceries() {
        dataSource.getGroceries(messId, month, year, groceries -> {
            totalGrocery = 0;
            userGrocery = 0;

            for (GroceryBatchDto batch : groceries) {
                if (batch.items != null) {
                    for (var item : batch.items) {
                        totalGrocery += item.price;
                        if (batch.userId.equals(currentUserId)) {
                            userGrocery += item.price;
                        }
                    }
                }
            }

            binding.tvTotalGrocery.setText("Total Grocery: " + totalGrocery);
            binding.tvUserGrocery.setText("Your Grocery: " + userGrocery);
            updateMealRateAndDue();

        }, e -> {
            binding.tvTotalGrocery.setText("Total Grocery: 0");
            binding.tvUserGrocery.setText("Your Grocery: 0");
        });
    }

    private void fetchMeals() {
        dataSource.getMeals(messId, month, year, meals -> {
            totalMealCount = 0;
            userMealCount = 0;

            for (MealTrackingDto meal : meals) {
                totalMealCount += meal.mealCount;
                if (meal.userId.equals(currentUserId)) userMealCount += meal.mealCount;
            }

            binding.tvTotalMeal.setText("Total Meals: " + totalMealCount);
            binding.tvUserMeal.setText("Your Meals: " + userMealCount);
            updateMealRateAndDue();

        }, e -> {
            binding.tvTotalMeal.setText("Total Meals: 0");
            binding.tvUserMeal.setText("Your Meals: 0");
            binding.tvMealRate.setText("Meal Rate: 0");
        });
    }

    private void fetchPayments() {
        dataSource.getPayments(messId, month, year, payments -> {
            totalPaymentsByUser = 0;
            for (PaymentDto p : payments) {
                if (p.userId.equals(currentUserId)) totalPaymentsByUser += p.amount;
            }
            updateMealRateAndDue();

        }, e -> binding.tvUserDue.setText("Your Due: 0"));
    }

    private void updateMealRateAndDue() {
        // Only calculate if meals and groceries have been loaded
        if (totalMealCount == 0) return;

        float mealRate = totalMealCount > 0 ? totalGrocery / totalMealCount : 0;
        float due = (userMealCount * mealRate) - totalPaymentsByUser;

        binding.tvMealRate.setText("Meal Rate: " + String.format("%.2f", mealRate));
        binding.tvUserDue.setText("Your Due: " + String.format("%.2f", due));
    }
}
