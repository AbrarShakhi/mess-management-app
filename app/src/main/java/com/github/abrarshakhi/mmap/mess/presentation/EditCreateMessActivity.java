package com.github.abrarshakhi.mmap.mess.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.core.constants.Months;
import com.github.abrarshakhi.mmap.databinding.ActivityEditCreateMessBinding;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.data.dto.MessDto;
import com.github.abrarshakhi.mmap.home.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.home.presentation.activities.HomeActivity;
import com.github.abrarshakhi.mmap.mess.presentation.AddMessActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class EditCreateMessActivity extends AppCompatActivity {

    private ActivityEditCreateMessBinding binding;
    private DataSource dataSource;
    private List<MessDto> messList = new ArrayList<>();
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

        dataSource = new DataSource(getApplicationContext());

        messItemAdapter = new MessItemAdapter(this, messList, mess -> selectMess(mess));
        binding.lvMessList.setAdapter(messItemAdapter);

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnAddMess.setOnClickListener(v -> startActivity(new Intent(this, AddMessActivity.class)));

        // Load currently selected mess info
        String currentMessId = dataSource.getCurrentMessId();
        if (!currentMessId.isEmpty()) {
            fetchMessInfo(currentMessId);
        }
    }

    private void fetchMessInfo(String messId) {
        dataSource.getMess(messId, new OnSuccessListener<MessDto>() {
            @Override
            public void onSuccess(MessDto mess) {
                if (mess == null) return;

                binding.tvMessName.setText(mess.name);
                binding.tvLocation.setText(mess.location);
                binding.tvMonth.setText(Months.getMonthName(mess.month));
                binding.tvYear.setText(String.valueOf(mess.year));
                binding.tvCurrency.setText(mess.currency);

                // Find the current user in mess members
                MessMemberDto member = null;
                if (mess.members != null) {
                    for (MessMemberDto m : mess.members) {
                        if (m.userId.equals(dataSource.getLoggedInUser().getUid())) {
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
                        binding.btnDeleteMess.setOnClickListener(v -> showLeaveMessDialog(mess.messId));
                    } else {
                        binding.btnDeleteMess.setVisibility(View.GONE);
                    }
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditCreateMessActivity.this, "Failed to fetch mess info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLeaveMessDialog(String messId) {
        new AlertDialog.Builder(this)
                .setTitle("Leave Mess")
                .setMessage("Are you sure you want to leave this mess?")
                .setPositiveButton("Yes", (dialog, which) -> leaveMess(messId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void leaveMess(String messId) {
        // Simple approach: remove current user from mess members
        dataSource.getMess(messId, mess -> {
            if (mess.members != null) {
                String uid = dataSource.getLoggedInUser().getUid();
                mess.members.removeIf(m -> m.userId.equals(uid));
                // Update mess in Firestore
                dataSource.createMess(mess, updated -> {
                    Toast.makeText(EditCreateMessActivity.this, "Left mess successfully", Toast.LENGTH_SHORT).show();
                    dataSource.saveCurrentMessId(""); // clear current mess
                    startActivity(new Intent(EditCreateMessActivity.this, HomeActivity.class));
                    finishAffinity();
                }, error -> Toast.makeText(EditCreateMessActivity.this, "Failed to leave mess", Toast.LENGTH_SHORT).show());
            }
        }, error -> Toast.makeText(EditCreateMessActivity.this, "Failed to fetch mess", Toast.LENGTH_SHORT).show());
    }

    private void selectMess(MessDto mess) {
        dataSource.saveCurrentMessId(mess.messId);
        startActivity(new Intent(this, HomeActivity.class));
        finishAffinity();
    }
}
