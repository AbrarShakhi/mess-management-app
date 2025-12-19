package com.github.abrarshakhi.mmap.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.core.constants.Months;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.MessDto;
import com.github.abrarshakhi.mmap.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.databinding.ActivityEditCreateMessBinding;
import com.github.abrarshakhi.mmap.presentation.adapters.MessItemAdapter;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class EditCreateMessActivity extends AppCompatActivity {

    private ActivityEditCreateMessBinding binding;
    private HomeDataSource homeDataSource;
    private List<MessDto> messList;
    private MessItemAdapter messItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityEditCreateMessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editCreateMess), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeDataSource = new HomeDataSource(getApplicationContext());
        messList = new ArrayList<>();
        messItemAdapter = new MessItemAdapter(this, messList, mess -> selectMess(mess));
        binding.lvMessList.setAdapter(messItemAdapter);

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnAddMess.setOnClickListener(v -> startActivity(new Intent(this, AddMessActivity.class)));

        // Load currently selected mess info
        String currentMessId = homeDataSource.getCurrentMessId();
        if (!currentMessId.isEmpty()) {
            fetchMessInfo(currentMessId);
        }
        loadJoinedMesses();
    }

    private void loadJoinedMesses() {
        String uid = homeDataSource.getLoggedInUser().getUid();
        homeDataSource.getMessesForUser(uid, messList -> {
            this.messList.clear();
            this.messList.addAll(messList);
            messItemAdapter.notifyDataSetChanged();
        }, error -> Toast.makeText(this, "Failed to fetch joined messes", Toast.LENGTH_SHORT).show());
    }


    private void fetchMessInfo(String messId) {
        homeDataSource.getMess(messId, new OnSuccessListener<MessDto>() {
            @Override
            public void onSuccess(MessDto mess) {
                if (mess == null) return;
                loadJoinedMesses();
                binding.tvMessName.setText(mess.name);
                binding.tvLocation.setText(mess.location);
                binding.tvMonth.setText(Months.getMonthName(mess.month));
                binding.tvYear.setText(String.valueOf(mess.year));
                binding.tvCurrency.setText(mess.currency);

                // Find the current user in mess members
                MessMemberDto member = null;
                if (mess.members != null) {
                    for (MessMemberDto m : mess.members) {
                        if (m.userId.equals(homeDataSource.getLoggedInUser().getUid())) {
                            member = m;
                            break;
                        }
                    }
                }

                if (member != null) {
                    if (MessMemberRole.LEFT.equals(member.role)) {
                        Toast.makeText(EditCreateMessActivity.this, "You left the mess", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (MessMemberRole.ADMIN.equals(member.role)) {
                        binding.btnDeleteMess.setVisibility(View.VISIBLE);
                        binding.btnDeleteMess.setOnClickListener(v -> deleteMess(mess.messId));
                    } else {
                        binding.btnDeleteMess.setVisibility(View.GONE);
                    }
                }
            }
        }, e -> Toast.makeText(EditCreateMessActivity.this, "Failed to fetch mess info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteMess(String messId) {
        homeDataSource.getMess(messId, mess -> {
            String uid = homeDataSource.getLoggedInUser().getUid();
            MessMemberDto currentUser = null;

            if (mess.members != null) {
                for (MessMemberDto member : mess.members) {
                    if (member.userId.equals(uid)) {
                        currentUser = member;
                        break;
                    }
                }
            }

            boolean canDelete = false;
            if (currentUser != null && MessMemberRole.ADMIN.equals(currentUser.role)) {
                canDelete = true; // Admin can always delete
            } else if (mess.memberIds != null && mess.memberIds.size() <= 1) {
                canDelete = true; // Single member mess
            }

            if (canDelete) {
                new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this mess? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        homeDataSource.deleteMess(messId,
                            result -> {
                                Toast.makeText(this, "Mess deleted successfully", Toast.LENGTH_SHORT).show();
                                homeDataSource.clear();
                                startActivity(new Intent(this, HomeActivity.class));
                                finishAffinity();
                            },
                            e -> Toast.makeText(this, "Failed to delete mess: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            } else {
                Toast.makeText(this, "Only admins can delete multi-member messes", Toast.LENGTH_SHORT).show();
            }
        }, e -> Toast.makeText(this, "Failed to fetch mess info", Toast.LENGTH_SHORT).show());
    }


    private void selectMess(MessDto mess) {
        homeDataSource.saveCurrentMessId(mess.messId);
        startActivity(new Intent(this, HomeActivity.class));
        finishAffinity();
    }
}
