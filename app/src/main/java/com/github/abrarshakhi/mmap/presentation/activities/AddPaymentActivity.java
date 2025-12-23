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
import com.github.abrarshakhi.mmap.core.constants.Months;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.PaymentDto;
import com.github.abrarshakhi.mmap.domain.model.MessMember;

import java.util.Arrays;
import java.util.List;

public class AddPaymentActivity extends AppCompatActivity {

    private EditText etAmount, etYear, etNote;
    private Spinner spinnerType, spinnerMonth;
    private HomeDataSource dataSource;
    private MessMember member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_payment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addPayment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dataSource = new HomeDataSource(this);

        etAmount = findViewById(R.id.etAmount);
        etYear = findViewById(R.id.etYear);
        etNote = findViewById(R.id.etNote);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerMonth = findViewById(R.id.spMonth); // replaced with Spinner in XML

        member = (MessMember) getIntent().getSerializableExtra("MEM");
        if (member == null) {
            Toast.makeText(this, "No member data provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup type spinner
        List<String> types = Arrays.asList("RENT", "UTILITY", "MEAL");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Setup month spinner
        List<String> months = Months.asList(); // ["Jan", "Feb", ..., "Dec"]
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Save button
        Button btnSave = findViewById(R.id.btnSavePayment);
        btnSave.setOnClickListener(v -> savePayment());
    }

    private void savePayment() {
        float amount;
        int monthIndex, year;

        try {
            amount = Float.parseFloat(etAmount.getText().toString().trim());
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            monthIndex = spinnerMonth.getSelectedItemPosition();
        } catch (Exception e) {
            Toast.makeText(this, "Invalid month", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            year = Integer.parseInt(etYear.getText().toString().trim());
            if (year < 2000) {
                Toast.makeText(this, "Invalid year", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid year", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = spinnerType.getSelectedItem().toString();
        String note = etNote.getText().toString().trim();

        PaymentDto payment = new PaymentDto(
            null,
            dataSource.getCurrentMessId(),
            member.userId,
            amount,
            monthIndex,
            year,
            type,
            note,
            System.currentTimeMillis()
        );

        // Save offline-first using HomeDataSource
        dataSource.addPaymentOfflineFirst(payment,
            localPayment -> {
                Toast.makeText(this, "Payment saved locally: " + localPayment.amount, Toast.LENGTH_SHORT).show();
                finish();
            },
            serverPayment -> Toast.makeText(this, "Payment synced with server: " + serverPayment.amount, Toast.LENGTH_SHORT).show(),
            error -> Toast.makeText(this, "Failed to save payment: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

}
