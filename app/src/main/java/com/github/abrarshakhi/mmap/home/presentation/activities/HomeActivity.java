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
import com.github.abrarshakhi.mmap.home.presentation.navigations.NavDestination;
import com.github.abrarshakhi.mmap.home.presentation.navigations.NavigationManager;
import com.github.abrarshakhi.mmap.mess.presentation.AddMessActivity;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NavigationManager navigation;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataSource = new DataSource(getApplicationContext());

        // Apply system window insets
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

        checkUserMess();

        binding.bnvMainNavBar.setOnItemSelectedListener(item -> {
            navigation.navigate(NavDestination.fromMenuId(item.getItemId()));
            return true;
        });
    }

    /**
     * Check if the current user has a mess, and navigate accordingly.
     */
    private void checkUserMess() {
        String currentMessId = dataSource.getCurrentMessId();

        if (currentMessId != null && !currentMessId.isEmpty()) {
            dataSource.getMess(currentMessId, messDto -> {
                if (messDto != null) {
                    navigation.navigate();
                } else {
                    navigateToAddMess();
                }
            }, e -> {
                Toast.makeText(HomeActivity.this,
                        "Error fetching mess: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                navigateToAddMess();
            });
        } else {
            navigateToAddMess();
        }
    }

    private void navigateToAddMess() {
        startActivity(new Intent(HomeActivity.this, AddMessActivity.class));
        finish();
    }
}
