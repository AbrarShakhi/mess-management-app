package com.github.abrarshakhi.mmap.mess.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.core.constants.Months;
import com.github.abrarshakhi.mmap.databinding.ActivityEditCreateMessBinding;
import com.github.abrarshakhi.mmap.home.domain.model.Mess;
import com.github.abrarshakhi.mmap.home.presentation.activities.HomeActivity;
import com.github.abrarshakhi.mmap.mess.data.repository.MessRepositoryImpl;
import com.github.abrarshakhi.mmap.mess.domain.usecase.DeleteMessUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.FetchMessInfoUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.ListMessesUseCase;
import com.github.abrarshakhi.mmap.mess.domain.usecase.SelectMessUseCase;

import java.util.ArrayList;
import java.util.List;

public class EditCreateMessActivity extends AppCompatActivity {
    private ActivityEditCreateMessBinding binding;
    private EditCreateMessViewModel viewModel;
    private MessItemAdapter messItemAdapter;
    private List<Mess> messList;

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

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (!modelClass.isAssignableFrom(EditCreateMessViewModel.class)) {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
                var repository = new MessRepositoryImpl(getApplicationContext());
                var fetchMessInfoUseCase = new FetchMessInfoUseCase(repository);
                var deleteMessUseCase = new DeleteMessUseCase(repository);
                var listMessesUseCase = new ListMessesUseCase(repository);
                var selectMessUseCase = new SelectMessUseCase(repository);
                return (T) new EditCreateMessViewModel(fetchMessInfoUseCase, deleteMessUseCase, listMessesUseCase, selectMessUseCase);
            }
        }).get(EditCreateMessViewModel.class);

        messList = new ArrayList<>();
        messItemAdapter = new MessItemAdapter(this, messList, viewModel);
        binding.lvMessList.setAdapter(messItemAdapter);

        binding.btnDeleteMess.setVisibility(View.GONE);
        viewModel.messInfoResult.observe(this, result -> {
            if (result.isSuccess()) {
                binding.tvMessName.setText(result.getMess().getName());
                binding.tvLocation.setText(result.getMess().getLocation());
                binding.tvMonth.setText(Months.getMonthName(result.getMess().getMonth()));
                binding.tvYear.setText(String.valueOf(result.getMess().getYear()));
                binding.tvCurrency.setText(result.getMess().getCurrency());

                var messMember = result.getMessMember();
                if (messMember.getRole().equals(MessMemberRole.LEFT)) {
                    Toast.makeText(getApplicationContext(), "you left the mess", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (messMember.getRole().equals(MessMemberRole.ADMIN)) {
                    binding.btnDeleteMess.setOnClickListener(v -> showDeleteMessDialog());
                    binding.btnDeleteMess.setVisibility(View.VISIBLE);
                }

            } else {
                Toast.makeText(getApplicationContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.fetchMessInfo();

        viewModel.listMess.observe(this, result -> {
            if (result.hasErr()) {
                Toast.makeText(EditCreateMessActivity.this, result.unwrapErr(), Toast.LENGTH_SHORT).show();
            } else {
                messList.clear();
                messList.addAll(result.unwrap());
                messItemAdapter.notifyDataSetChanged();
            }
        });
        viewModel.listMess();

        viewModel.messDeleteResult.observe(this, result -> {
            if (result.isSuccess) {
                startActivity(new Intent(EditCreateMessActivity.this, HomeActivity.class));
                finishAffinity();
            } else {
                Toast.makeText(getApplicationContext(), result.errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.messSelection.observe(this, result -> {
            if (result.hasErr()) {
                Toast.makeText(EditCreateMessActivity.this, result.unwrapErr(), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(EditCreateMessActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });
        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnAddMess.setOnClickListener(v -> startActivity(new Intent(EditCreateMessActivity.this, AddMessActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showDeleteMessDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Leave Mess")
            .setMessage("Are you sure you want to leave this mess?")
            .setPositiveButton("Yes", (d, w) -> viewModel.deleteMess())
            .setNegativeButton("Cancel", null)
            .show();
    }
}