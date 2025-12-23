package com.github.abrarshakhi.mmap.presentation.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.MealTrackingDto;
import com.github.abrarshakhi.mmap.domain.model.MonthYear;
import com.github.abrarshakhi.mmap.presentation.adapters.AddMealMemberAdapter;

import java.util.Map;

public class AddMealsActivity extends AppCompatActivity {

    private HomeDataSource ds;
    private AddMealMemberAdapter adapter;
    private MonthYear monthYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meals);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addMeal), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ds = new HomeDataSource(this);

        Spinner spinnerMealTime = findViewById(R.id.spinnerMealTime);
        ListView lvMembers = findViewById(R.id.lvMembers);
        Button btnSave = findViewById(R.id.btnSave);
        EditText etDay = findViewById(R.id.etDay);

        // Meal time spinner
        ArrayAdapter<String> mealTimeAdapter =
            new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"LUNCH", "DINNER"}
            );
        try {
            var m = getIntent().getIntExtra("M", -1);
            var y = getIntent().getIntExtra("Y", -1);
            monthYear = MonthYear.newValidInstance(m, y).unwrap();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to find Month Year", Toast.LENGTH_SHORT).show();
        }

        mealTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealTime.setAdapter(mealTimeAdapter);

        String messId = ds.getCurrentMessId();

        ds.listenMessMembersRealtime(
            messId,
            members -> {
                adapter = new AddMealMemberAdapter(this, members);
                lvMembers.setAdapter(adapter);
            },
            e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
        );

        btnSave.setOnClickListener(v -> {
            String mealTime = spinnerMealTime.getSelectedItem().toString();
            Map<String, Integer> counts = adapter.getMealCounts();

            String dayText = etDay.getText().toString().trim();

            if (dayText.isEmpty()) {
                Toast.makeText(this, "Please enter day", Toast.LENGTH_SHORT).show();
                return;
            }

            int day;
            try {
                day = Integer.parseInt(dayText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid day", Toast.LENGTH_SHORT).show();
                return;
            }

            if (day < 1 || day > 31) {
                Toast.makeText(this, "Day must be between 1 and 31", Toast.LENGTH_SHORT).show();
                return;
            }
            int month = monthYear.getMonth();
            int year = monthYear.getYear();

            for (Map.Entry<String, Integer> entry : counts.entrySet()) {

                if (entry.getValue() == 0) continue;

                MealTrackingDto dto = new MealTrackingDto(
                    System.currentTimeMillis(),
                    entry.getValue(),
                    mealTime,
                    day,
                    year,
                    month,
                    entry.getKey(),
                    messId,
                    null
                );

                ds.addMealOfflineFirst(
                    dto,
                    d -> {
                    },
                    d -> {
                    },
                    e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            finish();
        });
    }
}
