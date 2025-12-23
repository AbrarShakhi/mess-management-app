package com.github.abrarshakhi.mmap.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.MealTrackingDto;
import com.github.abrarshakhi.mmap.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.data.dto.PaymentDto;
import com.github.abrarshakhi.mmap.data.dto.UserDto;
import com.github.abrarshakhi.mmap.databinding.ActivityMemberInfoBinding;
import com.github.abrarshakhi.mmap.domain.model.MessMember;
import com.github.abrarshakhi.mmap.presentation.adapters.PaymentHistoryAdapter;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemberInfoActivity extends AppCompatActivity {

    private ActivityMemberInfoBinding binding;
    private MessMemberDto member;
    private String loggedInRole;
    private HomeDataSource dataSource;
    private PaymentHistoryAdapter paymentHistoryAdapter;
    private ListenerRegistration memberListener;

    private int currentMonth;
    private int currentYear;
    private ListenerRegistration paymentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMemberInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.memberInfo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dataSource = new HomeDataSource(this);
        var member_ = (MessMember) getIntent().getSerializableExtra("MEM");
        loggedInRole = getIntent().getStringExtra("ROLE");

        if (member_ == null) {
            Toast.makeText(this, "No member data provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        member = new MessMemberDto(member_.userId, dataSource.getCurrentMessId(), member_.role, member_.joinedAt, member_.houseRent, member_.utility);
        paymentHistoryAdapter = new PaymentHistoryAdapter(this, new ArrayList<>(), payment -> {
            // Show an AlertDialog with payment details
            String message = "Type: " + payment.type + "\nAmount: BDT: " + payment.amount + "\nMonth/Year: " + payment.month + "/" + payment.year + (payment.note != null && !payment.note.isEmpty() ? "\nNote: " + payment.note : "");

            new androidx.appcompat.app.AlertDialog.Builder(MemberInfoActivity.this).setTitle("Payment Details").setMessage(message).setPositiveButton("OK", null).show();
        });
        binding.lvPayments.setAdapter(paymentHistoryAdapter);

        initMessMember(member_);
    }

    private void initMessMember(MessMember member_) {
        if (MessMemberRole.ADMIN.equals(loggedInRole)) {
            binding.btnEditMember.setOnClickListener(v -> {
                var b = new Bundle();
                b.putSerializable("MEM", member_);
                startActivity(new Intent(MemberInfoActivity.this, EditMemberActivity.class).putExtras(b));
            });
            binding.btnAddPayment.setOnClickListener(v -> {
                var b = new Bundle();
                b.putSerializable("MEM", member_);
                startActivity(new Intent(MemberInfoActivity.this, AddPaymentActivity.class).putExtras(b));
            });
            binding.btnEditMember.setVisibility(View.VISIBLE);
            binding.btnAddPayment.setVisibility(View.VISIBLE);
        } else if (MessMemberRole.MANAGER.equals(loggedInRole)) {
            binding.btnAddPayment.setOnClickListener(v -> {
                var b = new Bundle();
                b.putSerializable("MEM", member_);
                startActivity(new Intent(MemberInfoActivity.this, AddPaymentActivity.class).putExtras(b));
            });
            binding.btnEditMember.setVisibility(View.GONE);
            binding.btnAddPayment.setVisibility(View.VISIBLE);
        } else {
            binding.btnEditMember.setVisibility(View.GONE);
            binding.btnAddPayment.setVisibility(View.GONE);
        }

        // 1️⃣ Fetch User Profile (full name, email, phone)
        dataSource.fetchUserProfile(member.userId, user -> {
            populateUserInfo(user);
            fetchCurrentMonthYear();
        }, e -> {
            Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            fetchCurrentMonthYear(); // fallback
        });
    }

    private void listenPaymentsRealtime() {
        paymentListener = dataSource.listenPaymentsRealtime(member.messId, currentMonth, currentYear, paymentList -> {
            // Filter only payments made by this member
            List<PaymentDto> memberPayments = new ArrayList<>();
            for (PaymentDto p : paymentList) {
                if (p.userId.equals(member.userId)) {
                    memberPayments.add(p);
                }
            }

            // Update the adapter
            paymentHistoryAdapter.clear();
            paymentHistoryAdapter.addAll(memberPayments);
            paymentHistoryAdapter.notifyDataSetChanged();
        }, e -> Toast.makeText(this, "Failed to load payments in realtime", Toast.LENGTH_SHORT).show());
    }

    private void populateUserInfo(UserDto user) {
        if (user == null) return;

        binding.tvFullName.setText(String.format("Full Name: %s", user.fullName));
        binding.tvEmail.setText(String.format("Email: %s", user.email));
        binding.tvPhone.setText(String.format("Phone: %s", user.phone));

        binding.tvMemberRole.setText(String.format("Role: %s", member.role));
        binding.tvJoinedAt.setText(String.format("Joined At: %s", DateFormat.getDateInstance().format(new Date(member.joinedAt))));
        binding.tvHouseRent.setText(String.format("House Rent: %s", member.houseRent));
        binding.tvUtility.setText(String.format("Utility: %s", member.utility));
    }

    private void fetchCurrentMonthYear() {
        // Get current month/year from member's mess
        dataSource.getCurrentMonthYearFromMess(member.messId, monthYear -> {
            currentMonth = monthYear.getMonth();
            currentYear = monthYear.getYear();
            fetchMeals();
            fetchPayments();
            listenPaymentsRealtime();
        }, e -> {
            Toast.makeText(this, "Failed to get current month/year", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchMeals() {
        dataSource.getMeals(member.messId, currentMonth, currentYear, mealList -> {
            int totalMeals = 0;
            int lunchCount = 0;
            int dinnerCount = 0;

            for (MealTrackingDto meal : mealList) {
                if (meal.userId.equals(member.userId)) {
                    totalMeals += meal.mealCount;
                    if ("LUNCH".equals(meal.mealTime)) lunchCount += meal.mealCount;
                    else if ("DINNER".equals(meal.mealTime)) dinnerCount += meal.mealCount;
                }
            }

            binding.tvTotalMeals.setText(String.format("Total Meals: %d", totalMeals));
            binding.tvLunchCount.setText(String.format("Lunch: %d", lunchCount));
            binding.tvDinnerCount.setText(String.format("Dinner: %d", dinnerCount));
        }, e -> Toast.makeText(this, "Failed to load meals", Toast.LENGTH_SHORT).show());
    }

    private void fetchPayments() {
        dataSource.getPayments(member.messId, currentMonth, currentYear, paymentList -> {

            // Filter only payments made by this member
            List<PaymentDto> memberPayments = new ArrayList<>();
            for (PaymentDto p : paymentList) {
                if (p.userId.equals(member.userId)) {
                    memberPayments.add(p);
                }
            }

            // Update the adapter
            paymentHistoryAdapter.clear();
            paymentHistoryAdapter.addAll(memberPayments);
            paymentHistoryAdapter.notifyDataSetChanged();

        }, e -> Toast.makeText(this, "Failed to load payments", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (paymentListener != null) {
            paymentListener.remove();
        }
        if (memberListener != null) {
            memberListener.remove();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        memberListener = dataSource.listenMessMembersRealtime(dataSource.getCurrentMessId(), members -> {
            for (var mem : members) {
                if (mem.userId.equals(member.userId)) {
                    member = new MessMemberDto(mem.userId, dataSource.getCurrentMessId(), mem.role, mem.joinedAt, mem.houseRent, mem.utility);
                    loggedInRole = mem.role;
                    initMessMember(mem);
                }
            }
        }, e -> {
        });
    }
}
