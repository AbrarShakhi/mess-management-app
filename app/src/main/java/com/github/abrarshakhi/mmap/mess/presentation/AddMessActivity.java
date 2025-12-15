package com.github.abrarshakhi.mmap.mess.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.Currencies;
import com.github.abrarshakhi.mmap.databinding.ActivityAddMessBinding;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.presentation.activities.HomeActivity;
import com.github.abrarshakhi.mmap.mess.data.repository.MessRepositoryImpl;
import com.github.abrarshakhi.mmap.mess.domain.usecase.CreateNewMessUseCase;

import java.util.List;

public class AddMessActivity extends AppCompatActivity {
    private ActivityAddMessBinding binding;
    private AddMessViewModel viewModel;

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

        viewModel = new AddMessViewModel(new CreateNewMessUseCase(new MessRepositoryImpl(this)));

        List<String> currencies = Currencies.asList();

        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            currencies
        );
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCurrency.setAdapter(currencyAdapter);

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnAddMess.setOnClickListener(v -> {
            viewModel.createMess(
                binding.etName.getText().toString(),
                binding.etLocation.getText().toString(),
                binding.etCity.getText().toString(),
                binding.spCurrency.getSelectedItem().toString()
            );
        });
        viewModel.createNewMessResult.observe(this, result -> {
            if (result.isSuccess()) {
                startActivity(new Intent(AddMessActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(AddMessActivity.this, result.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}