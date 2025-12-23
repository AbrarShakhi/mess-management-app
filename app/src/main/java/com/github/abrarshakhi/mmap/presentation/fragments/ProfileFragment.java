package com.github.abrarshakhi.mmap.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.abrarshakhi.mmap.data.dto.UserDto;
import com.github.abrarshakhi.mmap.presentation.activities.LoginActivity;
import com.github.abrarshakhi.mmap.databinding.FragmentProfileBinding;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.presentation.activities.EditCreateMessActivity;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private HomeDataSource homeDataSource;

    public ProfileFragment() {}

    @NonNull
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeDataSource = new HomeDataSource(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchUserInfo();

        binding.btnLogout.setOnClickListener(v -> logout());

        binding.btnEditMess.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), EditCreateMessActivity.class))
        );
    }

    /**
     * Fetch user info from DataSource and update UI
     */
    private void fetchUserInfo() {
        if (!homeDataSource.isLoggedIn()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = homeDataSource.getLoggedInUser().getUid();
        homeDataSource.fetchUserProfile(uid, userDto -> {
            if (userDto != null) {
                updateUI(userDto);
            } else {
                Toast.makeText(requireContext(), "User profile not found", Toast.LENGTH_SHORT).show();
            }
        }, e -> Toast.makeText(requireContext(),
                "Failed to fetch profile: " + e.getMessage(),
                Toast.LENGTH_SHORT).show());
    }

    /**
     * Update UI with user data
     */
    private void updateUI(UserDto userDto) {
        binding.tvProfileName.setText(userDto.fullName);
        binding.tvEmail.setText(userDto.email);
        binding.tvPhone.setText(
                (userDto.phone != null && !userDto.phone.isBlank())
                        ? userDto.phone
                        : "no phone added"
        );
    }

    /**
     * Logout user
     */
    private void logout() {
        homeDataSource.logout();
        homeDataSource.clear();
        startActivity(new Intent(requireActivity(), LoginActivity.class));
        requireActivity().finishAffinity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeDataSource = null;
        binding = null;
    }
}
