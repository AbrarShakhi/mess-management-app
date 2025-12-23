package com.github.abrarshakhi.mmap.presentation.activities;

import android.content.Intent;
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
import com.github.abrarshakhi.mmap.databinding.ActivityHomeBinding;
import com.github.abrarshakhi.mmap.presentation.navigations.NavDestination;
import com.github.abrarshakhi.mmap.presentation.navigations.NavigationManager;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NavigationManager navigation;
    private HomeDataSource homeDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        homeDataSource = new HomeDataSource(getApplicationContext());

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
        String currentMessId = homeDataSource.getCurrentMessId();

        if (currentMessId != null && !currentMessId.isBlank()) {
            homeDataSource.getMess(currentMessId, messDto -> {
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
            setAnyJoinedMess();
        }
    }

    private void setAnyJoinedMess() {
        var loggedInUserId = homeDataSource.getLoggedInUser().getUid();
        homeDataSource.getMessesForUser(loggedInUserId, result -> {
            for (var messDto : result) {
                for (var member : messDto.members) {
                    if (member.userId.equals(loggedInUserId) && !member.role.equals(MessMemberRole.LEFT)) {
                        homeDataSource.saveCurrentMessId(messDto.messId);
                        navigation.navigate();
                        return;
                    }
                }
            }
            navigateToAddMess();
        }, e -> navigateToAddMess());
    }

    private void navigateToAddMess() {
        startActivity(new Intent(HomeActivity.this, AddMessActivity.class));
        finish();
    }
}
