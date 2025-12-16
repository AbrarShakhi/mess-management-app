package com.github.abrarshakhi.mmap.mess.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.data.dto.MessDto;
import com.github.abrarshakhi.mmap.home.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.home.data.mapper.MessMapper;
import com.github.abrarshakhi.mmap.home.data.mapper.MessMemberMapper;
import com.github.abrarshakhi.mmap.home.domain.model.Mess;
import com.github.abrarshakhi.mmap.mess.domain.repository.CreateNewMessRepository;
import com.github.abrarshakhi.mmap.mess.domain.repository.DeleteMessRepository;
import com.github.abrarshakhi.mmap.mess.domain.repository.FetchMessInfoRepository;
import com.github.abrarshakhi.mmap.mess.domain.repository.FetchMessListRepository;
import com.github.abrarshakhi.mmap.mess.domain.repository.SelectMessRepository;
import com.github.abrarshakhi.mmap.mess.domain.usecase.request.CreateNewMessRequest;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.CreateNewMessResult;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessDeleteResult;
import com.github.abrarshakhi.mmap.mess.domain.usecase.result.MessInfoResult;

import java.util.ArrayList;
import java.util.List;

public class MessRepositoryImpl implements CreateNewMessRepository, FetchMessInfoRepository, DeleteMessRepository, FetchMessListRepository, SelectMessRepository {

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
            messDto.year = java.time.LocalDate.now().getYear();
            messDto.createdBy = currentUserId;
            MessDto createdMess = dataSource.createMess(messDto);

            MessMemberDto memberDto = new MessMemberDto(
                currentUserId,
                createdMess.messId,
                MessMemberRole.ADMIN,
                System.currentTimeMillis()
            );

            dataSource.addMember(createdMess.messId, memberDto);
            dataSource.saveCurrentMessId(createdMess.messId);

            return CreateNewMessResult.success();
        } catch (Exception e) {
            return CreateNewMessResult.failure(e.getMessage());
        }

    }

    @Override
    public MessInfoResult fetchMessInfo() {
        try {
            var messDto = dataSource.getMess(dataSource.getCurrentMessId());
            var mess = MessMapper.dtoToDomain(messDto);
            var userId = dataSource.getLoggedInUser().getUid();

            var messMembersDto = dataSource.getMembers(messDto.messId);
            for (var messMemberDto : messMembersDto) {
                if (messMemberDto.userId.equals(userId)) {
                    var messMember = MessMemberMapper.dtoToDomain(messMemberDto);
                    return MessInfoResult.success(mess, messMember);
                }
            }
            return MessInfoResult.failure("You are not in the mess");
        } catch (Exception e) {
            return MessInfoResult.failure(e.getMessage());
        }
    }

    @Override
    public MessDeleteResult deleteMess() {
        try {
            var userId = dataSource.getLoggedInUser().getUid();
            var messId = dataSource.getCurrentMessId();

            var messMembersDto = dataSource.getMembers(messId);
            for (var messMemberDto : messMembersDto) {
                if (!messMemberDto.userId.equals(userId)) {
                    if (!messMemberDto.role.equals(MessMemberRole.LEFT)) {
                        return new MessDeleteResult(false, "There is still a member in this mess unable to delete");
                    }
                }
            }
            dataSource.deleteMess(messId);
            return new MessDeleteResult(true, "");
        } catch (Exception e) {
            return new MessDeleteResult(false, e.getMessage());
        }
    }

    @Override
    public Outcome<List<Mess>, String> fetchMessList() {
        try {
            var userId = dataSource.getLoggedInUser().getUid();
            var messId = dataSource.getCurrentMessId();

            List<MessDto> messDtoList = dataSource.getMessesForUser(userId);
            List<Mess> messList = new ArrayList<>();
            for (var itm : messDtoList) {
                messList.add(MessMapper.dtoToDomain(itm));
            }
            return Outcome.success(messList);
        } catch (Exception e) {
            return Outcome.failure(e.getMessage());
        }
    }

    @Override
    public Outcome<Boolean, String> selectMess(String messId) {
        dataSource.saveCurrentMessId(messId);
        return Outcome.success(true);
    }
}

