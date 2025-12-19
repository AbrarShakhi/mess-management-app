package com.github.abrarshakhi.mmap.home.presentation.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.databinding.ActivityGroceryManageBinding;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.data.repository.GroceryRepositoryImpl;
import com.github.abrarshakhi.mmap.home.domain.model.AddedItem;
import com.github.abrarshakhi.mmap.home.domain.usecase.AddGroceryUseCase;
import com.github.abrarshakhi.mmap.home.domain.usecase.AddItemUseCase;
import com.github.abrarshakhi.mmap.home.presentation.adapters.addedGroceriesItemAdapter;
import com.github.abrarshakhi.mmap.home.presentation.viewmodel.FindMessViewModel;
import com.github.abrarshakhi.mmap.home.presentation.viewmodel.GroceryManageViewModel;

import java.util.ArrayList;
import java.util.List;

public class GroceryManageActivity extends AppCompatActivity {
    private ActivityGroceryManageBinding binding;
    private addedGroceriesItemAdapter adapter;
    private List<AddedItem> addedItems;
    private GroceryManageViewModel viewModel;

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

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(GroceryManageViewModel.class)) {
                    var dataSource = new DataSource(GroceryManageActivity.this);
                    var repository = new GroceryRepositoryImpl(dataSource);
                    var addGroceryUseCase = new AddGroceryUseCase(repository);
                    var addItemUseCase = new AddItemUseCase();

                    return (T) new GroceryManageViewModel(addGroceryUseCase, addItemUseCase);
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(GroceryManageViewModel.class);

        addedItems = new ArrayList<>();
        adapter = new addedGroceriesItemAdapter(this, addedItems);
        binding.lvGroceries.setAdapter(adapter);
        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnDone.setOnClickListener(v -> {
            viewModel.addGrocery(addedItems);
        });
        viewModel.addGroceryOutcome.observe(this, (result) -> {
            if (result.isOK()) {
                Toast.makeText(GroceryManageActivity.this, "Added", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(GroceryManageActivity.this, result.unwrapErr(), Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.addItemOutcome.observe(this, (result) -> {
            if (result.isOK()) {
                addedItems.add(result.unwrap());
                adapter.notifyDataSetChanged();
                binding.etItemName.setText("");
                binding.etPrice.setText("");
                binding.etQuantity.setText("");
            }  else {
                Toast.makeText(GroceryManageActivity.this, result.unwrapErr(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnAdd.setOnClickListener(v -> {
            String itemName = binding.etItemName.getText().toString();
            var priceOutcome = Outcome.make(() -> Float.parseFloat(binding.etPrice.getText().toString()));
            if (priceOutcome.hasErr()) {
                Toast.makeText(GroceryManageActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                return;
            }
            float price = priceOutcome.unwrap();
            String quantity = binding.etQuantity.getText().toString();
            viewModel.addItem(new AddedItem(quantity, price, itemName));
        });
    }
}