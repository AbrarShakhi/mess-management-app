package com.github.abrarshakhi.mmap.presentation.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.AddedItemDto;
import com.github.abrarshakhi.mmap.data.dto.GroceryBatchDto;
import com.github.abrarshakhi.mmap.databinding.ActivityGroceryManageBinding;
import com.github.abrarshakhi.mmap.domain.model.MonthYear;
import com.github.abrarshakhi.mmap.presentation.adapters.addedGroceriesItemAdapter;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GroceryManageActivity extends AppCompatActivity {

    private ActivityGroceryManageBinding binding;
    private addedGroceriesItemAdapter adapter;
    private List<AddedItemDto> addedItemDtos;
    private HomeDataSource dataSource;
    private MonthYear my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityGroceryManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.groceryManagement), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        var intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Required intent", Toast.LENGTH_SHORT).show();
            return;
        }
        my = MonthYear.newValidInstance(intent.getIntExtra("M", -1), intent.getIntExtra("Y", -1)).unwrap();

        dataSource = new HomeDataSource(this);
        addedItemDtos = new ArrayList<>();
        adapter = new addedGroceriesItemAdapter(this, addedItemDtos);
        binding.lvGroceries.setAdapter(adapter);

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> finish());

        // Add single item
        binding.btnAdd.setOnClickListener(v -> {
            String itemName = binding.etItemName.getText().toString();
            if (itemName.isEmpty()) {
                Toast.makeText(this, "Item name required", Toast.LENGTH_SHORT).show();
                return;
            }

            float price;
            try {
                price = Float.parseFloat(binding.etPrice.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }

            String quantity = binding.etQuantity.getText().toString();
            if (quantity.isEmpty()) {
                Toast.makeText(this, "Quantity required", Toast.LENGTH_SHORT).show();
                return;
            }

            AddedItemDto item = new AddedItemDto(quantity, price, itemName);
            addedItemDtos.add(item);
            adapter.notifyDataSetChanged();

            binding.etItemName.setText("");
            binding.etPrice.setText("");
            binding.etQuantity.setText("");
        });

        // Done button -> add grocery batch
        binding.btnDone.setOnClickListener(v -> {
            if (addedItemDtos.isEmpty()) {
                Toast.makeText(this, "Add at least one item", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = dataSource.getLoggedInUser();
            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String messId = dataSource.getCurrentMessId();
            if (messId.isEmpty()) {
                Toast.makeText(this, "No current mess selected", Toast.LENGTH_SHORT).show();
                return;
            }
            GroceryBatchDto batch = new GroceryBatchDto();
            batch.messId = messId;
            batch.userId = user.getUid();
            batch.items = new ArrayList<>(addedItemDtos);
            batch.timestamp = System.currentTimeMillis();
            batch.month = my.getMonth();
            batch.year = my.getYear();

            dataSource.addGroceryOfflineFirst(batch, dto -> {
                Toast.makeText(GroceryManageActivity.this, "Grocery batch added!", Toast.LENGTH_SHORT).show();
                finish();
            }, dto -> {
            }, e -> Toast.makeText(GroceryManageActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
