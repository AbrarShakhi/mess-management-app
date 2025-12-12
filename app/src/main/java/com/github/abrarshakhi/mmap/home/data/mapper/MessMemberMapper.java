package com.github.abrarshakhi.mmap.home.data.mapper;

import com.github.abrarshakhi.mmap.home.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.home.domain.model.MessMember;

public class MessMemberMapper {

    public static MessMember dtoToDomain(MessMemberDto dto) {
        return new MessMember(dto.userId, dto.role, dto.joinedAt);
    }

    public static MessMemberDto domainToDto(MessMember member) {
        return new MessMemberDto(member.getUserId(), member.getRole(), member.getJoinedAt());
    }
}
