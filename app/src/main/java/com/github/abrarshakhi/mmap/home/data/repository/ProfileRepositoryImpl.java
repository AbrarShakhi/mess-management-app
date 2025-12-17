package com.github.abrarshakhi.mmap.home.data.repository;

import com.github.abrarshakhi.mmap.auth.data.dto.UserDto;
import com.github.abrarshakhi.mmap.auth.data.mapper.UserMapper;
import com.github.abrarshakhi.mmap.auth.domain.model.User;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.domain.repository.FetchUserRepository;
import com.github.abrarshakhi.mmap.home.domain.repository.LogoutRepository;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.LogoutResult;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.UserInfoResult;
import com.google.android.gms.tasks.Task;

public class ProfileRepositoryImpl implements LogoutRepository, FetchUserRepository {
    private final DataSource ds;

    public ProfileRepositoryImpl(DataSource dataSource) {
        ds = dataSource;
    }

    @Override
    public LogoutResult logout() {
        ds.logout();
        ds.cleanPrefs();
        return LogoutResult.success();
    }

    @Override
    public UserInfoResult fetchInfo() {
        try {
            Task<UserDto> fetchTask = ds.fetchUserProfile(ds.getLoggedInUser().getUid());
            if (fetchTask == null) {
                logout();
                return UserInfoResult.failure("Unable to find user");
            }

            UserDto userDto = fetchTask.getResult();
            if (userDto == null) {
                logout();
                return UserInfoResult.failure("User profile not found");
            }
            User domainUser = UserMapper.dtoToDomain(ds.getLoggedInUser().getUid(), userDto);

            return UserInfoResult.success(domainUser);
        } catch (Exception e) {
            return UserInfoResult.failure("Something went wrong");
        }
    }
}
