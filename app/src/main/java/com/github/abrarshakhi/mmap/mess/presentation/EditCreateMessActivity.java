package com.github.abrarshakhi.mmap.mess.presentation;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.databinding.ActivityEditCreateMessBinding;

public class EditCreateMessActivity extends AppCompatActivity {
    private ActivityEditCreateMessBinding binding;

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



        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnAddMess.setOnClickListener(v -> {
            startActivity(new Intent(EditCreateMessActivity.this, AddMessActivity.class));
        });
    }
}