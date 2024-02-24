package com.minhapi.parkapi.web.dto.mapper;

import org.modelmapper.ModelMapper;

import com.minhapi.parkapi.entity.Vaga;
import com.minhapi.parkapi.web.dto.VagaCreateDto;
import com.minhapi.parkapi.web.dto.VagaResponseDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VagaMapper {

    public static VagaResponseDto toDto(Vaga vaga) {
        return new ModelMapper().map(vaga, VagaResponseDto.class);
    }

    public static Vaga toVaga(VagaCreateDto dto) {
        return new ModelMapper().map(dto, Vaga.class);
    }
    
}
