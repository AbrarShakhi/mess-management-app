package com.github.abrarshakhi.mmap.home.presentation.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.databinding.ActivityHomeBinding;
import com.github.abrarshakhi.mmap.home.presentation.navigations.NavDestination;
import com.github.abrarshakhi.mmap.home.presentation.navigations.NavigationManager;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

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

        navigation = new NavigationManager(
            getSupportFragmentManager(),
            R.id.flMainFrameLayout,
            getSupportActionBar()
        );

        setSupportActionBar(binding.topAppBar);

        binding.bnvMainNavBar.setOnItemSelectedListener(item -> {
            navigation.navigate(NavDestination.fromMenuId(item.getItemId()));
            return true;
        });

        navigation.navigate(NavDestination.HOME);
    }
}