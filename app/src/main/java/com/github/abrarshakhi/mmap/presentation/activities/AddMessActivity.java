package com.github.abrarshakhi.mmap.presentation.activities;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.Currencies;
import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.MessDto;
import com.github.abrarshakhi.mmap.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.databinding.ActivityAddMessBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class AddMessActivity extends AppCompatActivity {

    private ActivityAddMessBinding binding;
    private HomeDataSource homeDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddMessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addMess), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeDataSource = new HomeDataSource(getApplicationContext());

        // Setup currency spinner
        List<String> currencies = Currencies.asList();
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            currencies
        );
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCurrency.setAdapter(currencyAdapter);

        binding.btnCancel.setOnClickListener(v -> finish());

        binding.btnAddMess.setOnClickListener(v -> createMess());
    }

    private void createMess() {
        String name = binding.etName.getText().toString().trim();
        String location = binding.etLocation.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String currency = binding.spCurrency.getSelectedItem().toString();

        if (name.isEmpty() || location.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = homeDataSource.getLoggedInUser().getUid();

        MessMemberDto creatorMember = new MessMemberDto(
            userId,
            null,
            MessMemberRole.ADMIN,
            System.currentTimeMillis(),
            0,
            0
        );

        // Add member to the list
        List<MessMemberDto> members = new ArrayList<>();
        members.add(creatorMember);

        // Create the MessDto
        MessDto messDto = new MessDto();
        messDto.name = name;
        messDto.location = location;
        messDto.city = city;
        messDto.currency = currency;
        messDto.createdBy = userId;
        messDto.month = Calendar.getInstance().get(Calendar.MONTH);
        messDto.year = Calendar.getInstance().get(Calendar.YEAR);

        messDto.members = members;
        messDto.memberIds = new ArrayList<>();
        messDto.memberIds.add(userId);

        homeDataSource.createMess(messDto, new OnSuccessListener<MessDto>() {
            @Override
            public void onSuccess(MessDto result) {
                creatorMember.messId = result.messId;
                homeDataSource.saveCurrentMessId(result.messId);
                Toast.makeText(AddMessActivity.this, "Mess created successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddMessActivity.this, HomeActivity.class));
                finish();
            }
        }, e -> Toast.makeText(AddMessActivity.this, "Failed to create mess: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

}
