package com.github.abrarshakhi.mmap.home.presentation.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.abrarshakhi.mmap.R;
import com.github.abrarshakhi.mmap.databinding.ActivityHomeBinding;
import com.github.abrarshakhi.mmap.home.presentation.fragments.GroceryFragment;
import com.github.abrarshakhi.mmap.home.presentation.fragments.HomeFragment;
import com.github.abrarshakhi.mmap.home.presentation.fragments.ManagementFragment;
import com.github.abrarshakhi.mmap.home.presentation.fragments.MealsFragment;
import com.github.abrarshakhi.mmap.home.presentation.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.topAppBar);

        binding.bnvMainNavBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bniHome) {
                replaceFragment(new HomeFragment(), "Home");
            } else if (item.getItemId() == R.id.bniMeals) {
                replaceFragment(new MealsFragment(), "Meals Tracking");
            } else if (item.getItemId() == R.id.bniGrocery) {
                replaceFragment(new GroceryFragment(), "Grocery List");
            } else if (item.getItemId() == R.id.bniManagement) {
                replaceFragment(new ManagementFragment(), "Mess Management");
            } else if (item.getItemId() == R.id.bniProfile) {
                replaceFragment(new ProfileFragment(), "Profile");
            }
            return true;
        });
        replaceFragment(new HomeFragment(), "Home");
    }

    private void replaceFragment(Fragment fragment, String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flMainFrameLayout, fragment);
        fragmentTransaction.commit();
    }
}