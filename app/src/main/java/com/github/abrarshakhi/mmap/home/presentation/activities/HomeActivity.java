package com.github.abrarshakhi.mmap.home.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.databinding.ActivityHomeBinding;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.data.repository.FindMessRepositoryImpl;
import com.github.abrarshakhi.mmap.home.domain.usecase.FindMessUserCase;
import com.github.abrarshakhi.mmap.home.presentation.navigations.NavDestination;
import com.github.abrarshakhi.mmap.home.presentation.navigations.NavigationManager;
import com.github.abrarshakhi.mmap.home.presentation.viewmodel.FindMessViewModel;
import com.github.abrarshakhi.mmap.mess.presentation.AddMessActivity;
import com.github.abrarshakhi.mmap.mess.presentation.EditCreateMessActivity;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private FindMessViewModel viewModel;

    private NavigationManager navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.topAppBar);

        navigation = new NavigationManager(
            getSupportFragmentManager(),
            R.id.flMainFrameLayout,
            getSupportActionBar()
        );
        viewModel = new FindMessViewModel(
            new FindMessUserCase(
                new FindMessRepositoryImpl(
                    new DataSource(this)
                )
            )
        );

        viewModel.findMessResult.observe(this, result -> {
            if (result.isSuccess()) {
                if (result.isMessFound()) {
                    navigation.navigate();
                } else {
                    startActivity(new Intent(HomeActivity.this, AddMessActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(HomeActivity.this, result.getErrorMsg(), Toast.LENGTH_LONG).show();
            }
        });

        viewModel.findMess();

        binding.bnvMainNavBar.setOnItemSelectedListener(item -> {
            navigation.navigate(NavDestination.fromMenuId(item.getItemId()));
            return true;
        });
    }
}