package com.github.abrarshakhi.mmap.mess.presentation;

import android.content.Intent;
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
import com.github.abrarshakhi.mmap.databinding.ActivityAddMessBinding;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.presentation.activities.HomeActivity;
import com.github.abrarshakhi.mmap.home.data.dto.MessDto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class AddMessActivity extends AppCompatActivity {

    private ActivityAddMessBinding binding;
    private DataSource dataSource;

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

        dataSource = new DataSource(getApplicationContext());

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

        MessDto messDto = new MessDto();
        messDto.name = name;
        messDto.location = location;
        messDto.city = city;
        messDto.currency = currency;

        dataSource.createMess(messDto, new OnSuccessListener<MessDto>() {
            @Override
            public void onSuccess(MessDto result) {
                Toast.makeText(AddMessActivity.this, "Mess created successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddMessActivity.this, HomeActivity.class));
                finish();
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddMessActivity.this, "Failed to create mess: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
