package com.github.abrarshakhi.mmap.home.data.mapper;

import com.github.abrarshakhi.mmap.home.data.dto.MessDto;
import com.github.abrarshakhi.mmap.home.domain.model.Mess;

public class MessMapper {

    public static Mess dtoToDomain(MessDto dto) {
        return new Mess(dto.messId, dto.name, dto.location, dto.city,
                dto.month, dto.currency, dto.createdBy);
    }

    public static MessDto domainToDto(Mess domain) {
        return new MessDto(
                domain.getMessId(),
                domain.getName(),
                domain.getLocation(),
                domain.getCity(),
                domain.getMonth(),
                domain.getCurrency(),
                domain.getCreatedBy()
        );
    }
}
