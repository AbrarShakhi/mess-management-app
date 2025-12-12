package com.github.abrarshakhi.mmap.home.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.abrarshakhi.mmap.auth.domain.model.User;
import com.github.abrarshakhi.mmap.auth.presentation.activities.LoginActivity;
import com.github.abrarshakhi.mmap.databinding.FragmentProfileBinding;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.data.repository.ProfileRepositoryImpl;
import com.github.abrarshakhi.mmap.home.domain.usecase.FetchUserInfoUseCase;
import com.github.abrarshakhi.mmap.home.domain.usecase.LogoutUseCase;
import com.github.abrarshakhi.mmap.home.presentation.viewmodel.ProfileViewModel;
import com.github.abrarshakhi.mmap.mess.presentation.EditCreateMessActivity;

import org.jetbrains.annotations.Contract;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    private ProfileViewModel viewModel;

    protected ProfileFragment() {
    }

    @NonNull
    @Contract(" -> new")
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var dataSource = new DataSource(requireContext());
        var repo = new ProfileRepositoryImpl(dataSource);
        var logoutUseCase = new LogoutUseCase(repo);
        var fetchUserProfileUseCase = new FetchUserInfoUseCase(repo);
        viewModel = new ProfileViewModel(logoutUseCase, fetchUserProfileUseCase);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.userInfoResult.observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                User user = result.getUser();
                if (user != null) {
                    binding.tvProfileName.setText(user.getFullName());
                    binding.tvEmail.setText(user.getEmail());
                    binding.tvPhone.setText(
                            (!user.getPhone().isBlank())
                                    ? user.getPhone()
                                    : "no phone added");
                }
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.fetchInfo();

        viewModel.logoutResult.observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                startActivity(new Intent(requireActivity(), LoginActivity.class));
                requireActivity().finishAffinity();
            } else {
                Toast.makeText(requireContext(), result.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            viewModel.logout();
        });

        binding.btnEditMess.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), EditCreateMessActivity.class));
        });
    }



}