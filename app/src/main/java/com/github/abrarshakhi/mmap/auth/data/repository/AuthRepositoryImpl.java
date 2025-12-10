package com.github.abrarshakhi.mmap.auth.data.repository;

import com.github.abrarshakhi.mmap.auth.data.datasourse.RemoteDataSource;
import com.github.abrarshakhi.mmap.auth.data.mapper.UserMapper;
import com.github.abrarshakhi.mmap.auth.data.model.UserDto;
import com.github.abrarshakhi.mmap.auth.domain.model.User;
import com.github.abrarshakhi.mmap.auth.domain.repository.LoginRepository;
import com.github.abrarshakhi.mmap.auth.domain.repository.SignupRepository;
import com.github.abrarshakhi.mmap.auth.domain.usecase.request.LoginRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.request.SignupRequest;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.LoginResult;
import com.github.abrarshakhi.mmap.auth.domain.usecase.result.SignupResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepositoryImpl implements LoginRepository, SignupRepository {
    private final RemoteDataSource remoteDataSource;

    public AuthRepositoryImpl(RemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    @Override
    public SignupResult signup(SignupRequest request) {
        try {
            Task<AuthResult> authTask = remoteDataSource.signup(
                request.getEmail(),
                request.getPassword()
            );
            Tasks.await(authTask);

            if (!authTask.isSuccessful()) {
                return SignupResult.failure(authTask.getException().getMessage());
            }

            FirebaseUser user = authTask.getResult().getUser();
            if (user == null) {
                return SignupResult.failure("Unable to create user");
            }

            Task<Void> sendEmailTask = remoteDataSource.sendEmailVerification(user);
            if (!sendEmailTask.isSuccessful()) {
                return SignupResult.failure(authTask.getException().getMessage());
            }

            String uid = user.getUid();

            UserDto dto = new UserDto(
                request.getFullName(),
                request.getEmail(),
                request.getPhone()
            );

            Task<Void> writeTask = remoteDataSource.saveUserProfile(uid, dto);
            Tasks.await(writeTask);

            if (!writeTask.isSuccessful()) {
                return SignupResult.failure(authTask.getException().getMessage());
            }

            var domainUser = UserMapper.dtoToDomain(uid, dto);

            return SignupResult.success(domainUser);

        } catch (Exception e) {
            return SignupResult.failure(e.getMessage());
        }
    }

    @Override
    public LoginResult login(LoginRequest request) {
        try {
            Task<AuthResult> authTask = remoteDataSource.login(
                request.getEmail(),
                request.getPassword()
            );
            Tasks.await(authTask);

            if (!authTask.isSuccessful()) {
                return LoginResult.failure(authTask.getException().getMessage());
            }

            if (remoteDataSource.isEmailVerified(remoteDataSource.getCurrentUser())) {
                return LoginResult.failure("Please verify your email");
            }

            Task<UserDto> fetchTask = remoteDataSource.fetchUserProfile(remoteDataSource.getCurrentUser().getUid());
            if (fetchTask == null) {
                remoteDataSource.logout();
                return LoginResult.failure("Unable to find user");
            }
            Tasks.await(fetchTask);

            UserDto userDto = fetchTask.getResult();
            if (userDto == null) {
                remoteDataSource.logout();
                return LoginResult.failure("User profile not found");
            }

            User domainUser = UserMapper.dtoToDomain(remoteDataSource.getCurrentUser().getUid(), userDto);

            return LoginResult.success(domainUser);
        } catch (Exception e) {
            return LoginResult.failure(e.getMessage());
        }
    }

    @Override
    public LoginResult isLoggedIn() {
        try {
            if (!remoteDataSource.isLoggedIn()) {
                return LoginResult.failure("Login first");
            }
            if (remoteDataSource.isEmailVerified(remoteDataSource.getCurrentUser())) {
                return LoginResult.failure("Please verify your email");
            }

            Task<UserDto> fetchTask = remoteDataSource.fetchUserProfile(remoteDataSource.getCurrentUser().getUid());
            if (fetchTask == null) {
                remoteDataSource.logout();
                return LoginResult.failure("Unable to find user");
            }
            Tasks.await(fetchTask);

            UserDto userDto = fetchTask.getResult();
            if (userDto == null) {
                remoteDataSource.logout();
                return LoginResult.failure("User profile not found");
            }
            User domainUser = UserMapper.dtoToDomain(remoteDataSource.getCurrentUser().getUid(), userDto);

            return LoginResult.success(domainUser);
        } catch (Exception e) {
            return LoginResult.failure(e.getMessage());
        }
    }

}
