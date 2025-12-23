package com.github.abrarshakhi.mmap.presentation.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.MessDto;
import com.github.abrarshakhi.mmap.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.domain.model.MessMember;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;
import java.util.List;

public class EditMemberActivity extends AppCompatActivity {

    private Spinner spRole;
    private EditText etHouseRent, etUtility;
    private Button btnSaveMember, btnRemoveMember;

    private MessMemberDto member;
    private HomeDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_member);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editMember), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dataSource = new HomeDataSource(this);

        // Find views
        spRole = findViewById(R.id.spRole);
        etHouseRent = findViewById(R.id.etHouseRent);
        etUtility = findViewById(R.id.etUtility);
        btnSaveMember = findViewById(R.id.btnSaveMember);
        btnRemoveMember = findViewById(R.id.btnRemoveMember);

        // Set up spinner with roles
        List<String> roles = Arrays.asList(
            MessMemberRole.ADMIN,
            MessMemberRole.DEFAULT,
            MessMemberRole.MANAGER
        );
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        // Get member data from intent
        var member_ = (MessMember) getIntent().getSerializableExtra("MEM");

        if (member_ == null) {
            Toast.makeText(this, "No member data provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        member = new MessMemberDto(member_.userId, dataSource.getCurrentMessId(), member_.role, member_.joinedAt, member_.houseRent, member_.utility);


        // Populate fields
        spRole.setSelection(roles.indexOf(member.role));
        etHouseRent.setText(String.valueOf(member.houseRent));
        etUtility.setText(String.valueOf(member.utility));

        btnSaveMember.setOnClickListener(v -> saveMemberChanges());
        btnRemoveMember.setOnClickListener(v -> RemoveMember());
    }

    private void RemoveMember() {
        member.role = MessMemberRole.LEFT;

        member.houseRent = 0;
        member.utility = 0;

        dataSource.getMess(member.messId, new OnSuccessListener<MessDto>() {
            @Override
            public void onSuccess(MessDto mess) {
                if (mess.members != null) {
                    boolean found = false;
                    for (int i = 0; i < mess.members.size(); i++) {
                        if (mess.members.get(i).userId.equals(member.userId)) {
                            mess.members.set(i, member);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        mess.members.add(member);
                    }

                    dataSource.editMemberOfflineCapable(
                        member.messId,
                        member,
                        localMsg -> {
                            Toast.makeText(EditMemberActivity.this, "Changes saved locally", Toast.LENGTH_SHORT).show();
                            finish();
                        },
                        e -> Toast.makeText(EditMemberActivity.this, "Failed to update member: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );

                }
            }
        }, e -> Toast.makeText(EditMemberActivity.this, "Failed to fetch mess", Toast.LENGTH_SHORT).show());
    }

    private void saveMemberChanges() {
        member.role = spRole.getSelectedItem().toString();

        String houseRentStr = etHouseRent.getText().toString().trim();
        float houseRent;
        try {
            houseRent = Float.parseFloat(houseRentStr);
            if (houseRent < 0) {
                Toast.makeText(this, "House Rent cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input for House Rent", Toast.LENGTH_SHORT).show();
            return;
        }
        member.houseRent = houseRent;

        String utilityStr = etUtility.getText().toString().trim();
        float utility;
        try {
            utility = Float.parseFloat(utilityStr);
            if (utility < 0) {
                Toast.makeText(this, "Utility cannot be negative", Toast.LENGTH_SHORT).show();
                return; // Stop saving
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input for Utility", Toast.LENGTH_SHORT).show();
            return; // Stop saving
        }
        member.utility = utility;

        dataSource.getMess(member.messId, new OnSuccessListener<MessDto>() {
            @Override
            public void onSuccess(MessDto mess) {
                if (mess.members != null) {
                    boolean found = false;
                    for (int i = 0; i < mess.members.size(); i++) {
                        if (mess.members.get(i).userId.equals(member.userId)) {
                            mess.members.set(i, member);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        mess.members.add(member);
                    }

                    dataSource.editMemberOfflineCapable(
                        member.messId,
                        member,
                        localMsg -> {
                            Toast.makeText(EditMemberActivity.this, "Changes saved locally", Toast.LENGTH_SHORT).show();
                            finish();
                        },
                        e -> Toast.makeText(EditMemberActivity.this, "Failed to update member: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );

                }
            }
        }, e -> Toast.makeText(EditMemberActivity.this, "Failed to fetch mess", Toast.LENGTH_SHORT).show());
    }
}
