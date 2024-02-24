package com.minhapi.parkapi.web.dto.mapper;

import org.modelmapper.ModelMapper;

import com.minhapi.parkapi.entity.ClienteVaga;
import com.minhapi.parkapi.web.dto.EstacionamentoCreateDto;
import com.minhapi.parkapi.web.dto.EstacionamentoResponseDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstacionamentoMapper {

    public static EstacionamentoResponseDto toDto(ClienteVaga vaga) {
        return new ModelMapper().map(vaga, EstacionamentoResponseDto.class);
    }

    public static ClienteVaga toClienteVaga(EstacionamentoCreateDto dto) {
        return new ModelMapper().map(dto, ClienteVaga.class);
    }
    
}
