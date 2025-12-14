package com.github.abrarshakhi.mmap.home.data.repository;

import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.domain.repository.FindMessRepository;
import com.github.abrarshakhi.mmap.home.domain.usecase.result.FindMessResult;
import com.google.firebase.FirebaseNetworkException;

import java.util.concurrent.ExecutionException;

public class FindMessRepositoryImpl implements FindMessRepository {
    private final DataSource ds;

    public FindMessRepositoryImpl(DataSource dataSource) {
        ds = dataSource;
    }

    @Override
    public FindMessResult findMess() {
        try {
            if (!ds.getCurrentMessId().isEmpty()) {
                return FindMessResult.success(true);
            }
            var userId = ds.getLoggedInUser().getUid();
            var messes = ds.getMessesForUser(userId);
            if (messes.isEmpty()) {
                return FindMessResult.success(false);
            }
            ds.saveCurrentMessId(messes.get(0).messId);
            return FindMessResult.success(true);
        } catch (ExecutionException ee) {
            if (ee.getCause() instanceof FirebaseNetworkException) {
                return FindMessResult.failure("No internet connection");
            }
            return FindMessResult.failure(ee.getMessage());
        } catch (Exception e) {
            return FindMessResult.failure(e.getMessage());
        }
    }
}