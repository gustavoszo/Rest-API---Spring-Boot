package com.minhapi.parkapi.web.dto.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import com.minhapi.parkapi.web.dto.PageableDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageableMapper {
    
    @SuppressWarnings("rawtypes")
    public static PageableDto toDto(Page page) {
        return new ModelMapper().map(page, PageableDto.class);
    }

}
