package com.github.abrarshakhi.mmap.auth.data.repository;

import com.github.abrarshakhi.mmap.auth.data.datasourse.AuthDataSource;
import com.github.abrarshakhi.mmap.auth.data.dto.UserDto;
import com.github.abrarshakhi.mmap.auth.data.mapper.UserMapper;
import com.github.abrarshakhi.mmap.auth.domain.model.User;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;
import com.github.abrarshakhi.mmap.auth.domain.usecase.request.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.request.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.SignupResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;

public class AuthRepositoryImpl implements LoginRepository, SignupRepository {
    private final AuthDataSource authDataSource;

    public AuthRepositoryImpl(AuthDataSource authDataSource) {
        this.authDataSource = authDataSource;
    }

    @Override
    public SignupResult signup(SignupRequest request) {
        try {
            Task<AuthResult> authTask = authDataSource.signup(
                request.getEmail(),
                request.getPassword()
            );

            if (!authTask.isSuccessful()) {
                return SignupResult.failure(authTask.getException().getMessage());
            }

            FirebaseUser user = authTask.getResult().getUser();
            if (user == null) {
                return SignupResult.failure("Unable to create user");
            }

            Task<Void> sendEmailTask = authDataSource.sendEmailVerification(user);
            if (!sendEmailTask.isSuccessful()) {
                return SignupResult.failure(authTask.getException().getMessage());
            }

            String uid = user.getUid();

            UserDto dto = new UserDto(
                request.getFullName(),
                request.getEmail(),
                request.getPhone()
            );

            Task<Void> writeTask = authDataSource.saveUserProfile(uid, dto);
            if (!writeTask.isSuccessful()) {
                return SignupResult.failure(authTask.getException().getMessage());
            }

            var domainUser = UserMapper.dtoToDomain(uid, dto);

            return SignupResult.success(domainUser);
        } catch (ExecutionException ee) {
            if (ee.getCause() instanceof FirebaseNetworkException) {
                return SignupResult.failure("No internet connection");
            }
            return SignupResult.failure(ee.getMessage());
        } catch (Exception e) {
            return SignupResult.failure(e.getMessage());
        }
    }

    @Override
    public LoginResult login(LoginRequest request) {
        try {
            Task<AuthResult> authTask = authDataSource.login(
                request.getEmail(),
                request.getPassword()
            );

            if (!authTask.isSuccessful()) {
                return LoginResult.failure(authTask.getException().getMessage());
            }

            FirebaseUser user = authTask.getResult().getUser();
            if (user == null) {
                return LoginResult.failure("Unable to find user");
            }

            if (authDataSource.isNotEmailVerified(user)) {
                return LoginResult.failure("Please verify your email");
            }

            Task<UserDto> fetchTask = authDataSource.fetchUserProfile(user.getUid());
            if (fetchTask == null) {
                authDataSource.logout();
                return LoginResult.failure("Unable to find user");
            }

            UserDto userDto = fetchTask.getResult();
            if (userDto == null) {
                authDataSource.logout();
                return LoginResult.failure("User profile not found");
            }

            User domainUser = UserMapper.dtoToDomain(user.getUid(), userDto);

            return LoginResult.success(domainUser);
        } catch (ExecutionException ee) {
            if (ee.getCause() instanceof FirebaseNetworkException) {
                return LoginResult.failure("No internet connection");
            }
            return LoginResult.failure(ee.getMessage());
        } catch (Exception e) {
            return LoginResult.failure(e.getMessage());
        }
    }

    @Override
    public LoginResult isLoggedIn() {
        try {
            if (!authDataSource.isLoggedIn()) {
                return LoginResult.failure("");
            }
            if (authDataSource.isNotEmailVerified(authDataSource.getLoggedInUser())) {
                return LoginResult.failure("Please verify your email");
            }

            Task<UserDto> fetchTask = authDataSource.fetchUserProfile(authDataSource.getLoggedInUser().getUid());
            if (fetchTask == null) {
                authDataSource.logout();
                return LoginResult.failure("Unable to find user");
            }

            UserDto userDto = fetchTask.getResult();
            if (userDto == null) {
                authDataSource.logout();
                return LoginResult.failure("User profile not found");
            }
            User domainUser = UserMapper.dtoToDomain(authDataSource.getLoggedInUser().getUid(), userDto);

            return LoginResult.success(domainUser);
        } catch (Exception e) {
            return LoginResult.failure(e.getMessage());
        }
    }

}
