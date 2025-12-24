package com.github.abrarshakhi.mmap.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.data.dto.PaymentDto;
import com.github.abrarshakhi.mmap.databinding.FragmentHomeBinding;
import com.github.abrarshakhi.mmap.presentation.adapters.PaymentHistoryAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeDataSource dataSource;

    private PaymentHistoryAdapter paymentAdapter;
    private ListenerRegistration paymentListener;

    private String messId;
    private String userId;
    private int currentMonth;
    private int currentYear;

    private float totalGroceryCost = 0f;
    private int totalMeals = 0;
    private int myMeals = 0;

    private float paidRent = 0f;
    private float paidUtility = 0f;
    private float paidMeal = 0f;

    private float mealRate = 0f;
    private MessMemberDto myMember;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new HomeDataSource(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = dataSource.getLoggedInUser();
        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        userId = user.getUid();
        messId = dataSource.getCurrentMessId();

        if (messId == null || messId.isEmpty()) {
            Toast.makeText(requireContext(), "No active mess", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchCurrentMonthYear();
    }

    private void setupPaymentList() {
        paymentAdapter = new PaymentHistoryAdapter(
                requireContext(),
                new ArrayList<>(),
                payment -> {
                    String msg =
                            "Type: " + payment.type +
                                    "\nAmount: BDT " + payment.amount +
                                    "\nMonth/Year: " + payment.month + "/" + payment.year +
                                    (payment.note != null && !payment.note.isEmpty()
                                            ? "\nNote: " + payment.note
                                            : "");

                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Payment Details")
                            .setMessage(msg)
                            .setPositiveButton("OK", null)
                            .show();
                }
        );

        binding.lvPayments.setAdapter(paymentAdapter);
    }

    private void fetchMyMessMember() {
        dataSource.listenMessMembersRealtime(
                messId,
                members -> {
                    for (var m : members) {
                        if (m.userId.equals(userId)) {
                            myMember = new MessMemberDto(
                                    m.userId,
                                    m.messId,
                                    m.role,
                                    m.joinedAt,
                                    m.houseRent,
                                    m.utility
                            );
                            loadPayments();
                            setupPaymentList();
                            fetchGroceries();
                            fetchMeals();
                            listenPaymentsRealtime();
                            break;
                        }
                    }
                },
                e -> Toast.makeText(requireContext(), "Failed to load member info", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateRentUtilityUI() {
        if (myMember == null) return;

        calculateDues();
    }


    private void calculateMealRate() {
        if (totalMeals == 0) {
            mealRate = 0;
        } else {
            mealRate = totalGroceryCost / totalMeals;
        }

        binding.tvMealRate.setText("Meal Rate: " + mealRate);
        calculateDues();
    }


    private void fetchMeals() {
        dataSource.getMeals(
                messId,
                currentMonth,
                currentYear,
                meals -> {
                    totalMeals = 0;
                    myMeals = 0;

                    for (var m : meals) {
                        totalMeals += m.mealCount;
                        if (m.userId.equals(userId)) {
                            myMeals += m.mealCount;
                        }
                    }

                    binding.tvTotalMeal.setText("Total Meals: " + totalMeals);
                    binding.tvUserMeal.setText("Your Meals: " + myMeals);

                    calculateMealRate();
                },
                e -> Toast.makeText(requireContext(), "Failed to load meals", Toast.LENGTH_SHORT).show()
        );
    }


    private void fetchGroceries() {
        dataSource.getGroceries(
                messId,
                currentMonth,
                currentYear,
                groceries -> {
                    float myGrocery = 0f;

                    for (var batch : groceries) {
                        if (batch.items != null) {
                            for (var item : batch.items) {
                                totalGroceryCost += item.price;
                                if (userId.equals(batch.userId)) {
                                    myGrocery += item.price;
                                }
                            }
                        }
                    }
                    binding.tvUserGrocery.setText("Your Grocery: " + myGrocery);
                    binding.tvTotalGrocery.setText("Total Grocery: " + totalGroceryCost);
                    calculateMealRate();
                },
                e -> Toast.makeText(requireContext(), "Failed to load groceries", Toast.LENGTH_SHORT).show()
        );
    }


    private void fetchCurrentMonthYear() {
        dataSource.getCurrentMonthYearFromMess(
                messId,
                monthYear -> {
                    currentMonth = monthYear.getMonth();
                    currentYear = monthYear.getYear();
                    fetchMyMessMember();
                },
                e -> Toast.makeText(
                        requireContext(),
                        "Failed to load month/year",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void loadPayments() {
        dataSource.getPayments(
                messId,
                currentMonth,
                currentYear,
                this::updatePaymentList,
                e -> Toast.makeText(
                        requireContext(),
                        "Failed to load payments",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void listenPaymentsRealtime() {
        paymentListener = dataSource.listenPaymentsRealtime(
                messId,
                currentMonth,
                currentYear,
                this::updatePaymentList,
                e -> Toast.makeText(
                        requireContext(),
                        "Realtime payment error",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void updatePaymentList(List<PaymentDto> allPayments) {

        List<PaymentDto> myPayments = new ArrayList<>();

        paidRent = 0f;
        paidUtility = 0f;
        paidMeal = 0f;

        for (PaymentDto p : allPayments) {
            if (p.userId.equals(userId)) {
                myPayments.add(p);

                switch (p.type) {
                    case "RENT":
                        paidRent += p.amount;
                        break;
                    case "UTILITY":
                        paidUtility += p.amount;
                        break;
                    case "MEAL":
                    case "GROCERY":
                        paidMeal += p.amount;
                        break;
                }
            }
        }

        myPayments.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));

        paymentAdapter.clear();
        paymentAdapter.addAll(myPayments);
        paymentAdapter.notifyDataSetChanged();

        calculateDues();
        updateRentUtilityUI();
    }

    private void calculateDues() {
        if (myMember == null) return;

        float rentDue = myMember.houseRent - paidRent;
        float utilityDue = myMember.utility - paidUtility;
        float mealDue = (myMeals * mealRate) - paidMeal;

        binding.tvRentDue.setText("Rent Due: " + rentDue);
        binding.tvUtilityDue.setText("Utility Due: " + utilityDue);
        binding.tvMealDue.setText("Meal Due: " + mealDue);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (paymentListener != null) {
            paymentListener.remove();
        }
        binding = null;
    }
}
