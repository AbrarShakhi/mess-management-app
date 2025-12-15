package com.github.abrarshakhi.mmap.mess.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.data.dto.MessDto;
import com.github.abrarshakhi.mmap.home.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.mess.domain.repository.CreateNewMessRepository;
import com.github.abrarshakhi.mmap.mess.domain.usecase.request.CreateNewMessRequest;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.CreateNewMessResult;

public class MessRepositoryImpl implements CreateNewMessRepository {

    private final DataSource dataSource;

    public MessRepositoryImpl(Context context) {
        this.dataSource = new DataSource(context);
    }

    @Override
    public CreateNewMessResult createMess(@NonNull CreateNewMessRequest request) {
        try {
            String currentUserId = dataSource.getLoggedInUser().getUid();

            MessDto messDto = new MessDto();
            messDto.name = request.name;
            messDto.location = request.location;
            messDto.city = request.city;
            messDto.currency = request.currency;
            messDto.month = java.time.LocalDate.now().getMonthValue() - 1;
            messDto.createdBy = currentUserId;
            MessDto createdMess = dataSource.createMess(messDto);

            MessMemberDto memberDto = new MessMemberDto(
                currentUserId,
                createdMess.messId,
                "ADMIN",
                System.currentTimeMillis()
            );

            dataSource.addMember(createdMess.messId, memberDto);
            dataSource.saveCurrentMessId(createdMess.messId);

            return CreateNewMessResult.success();
        } catch (Exception e) {
            return CreateNewMessResult.failure(e.getMessage());
        }

    }
}

