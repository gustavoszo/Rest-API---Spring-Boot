package com.minhapi.parkapi.web.dto.mapper;

import org.modelmapper.ModelMapper;

import com.minhapi.parkapi.entity.Cliente;
import com.minhapi.parkapi.web.dto.ClienteCreateDto;
import com.minhapi.parkapi.web.dto.ClienteResponseDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteMapper {
    
    public static Cliente toCliente(ClienteCreateDto dto) {
        return new ModelMapper().map(dto, Cliente.class);
    }
    
    public static ClienteResponseDto toDto(Cliente cliente) {
        return new ModelMapper().map(cliente, ClienteResponseDto.class);
    }

}
