package com.github.abrarshakhi.mmap.presentation.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.databinding.ActivityAddMemberBinding;

public class AddMemberActivity extends AppCompatActivity {
    private ActivityAddMemberBinding binding;
    private HomeDataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddMemberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addMember), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ds = new HomeDataSource(this);
        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnAddMember.setOnClickListener(v -> {

            String messId = ds.getCurrentMessId();
            String email = binding.etEmailAddress.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            ds.findUserByEmail(
                email,
                userId -> {
                    long now = System.currentTimeMillis();

                    MessMemberDto member = new MessMemberDto(
                        userId,
                        messId,
                        MessMemberRole.DEFAULT,
                        now,
                        0f,
                        0f
                    );

                    ds.addOrUpdateMemberOffline(
                        messId,
                        member,
                        msg -> runOnUiThread(() ->
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        ),
                        msg -> runOnUiThread(() ->
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        ),
                        e -> runOnUiThread(() ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                        )
                    );
                },
                e -> runOnUiThread(() ->
                    Toast.makeText(
                        this,
                        "User does not exist",
                        Toast.LENGTH_SHORT
                    ).show()
                )
            );
        });

    }
}