package com.minhapi.parkapi.web.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import com.minhapi.parkapi.entity.Usuario;
import com.minhapi.parkapi.web.dto.UsuarioCreateDto;
import com.minhapi.parkapi.web.dto.UsuarioResponseDto;

public class UsuarioMapper {

    public static Usuario toUsuario(UsuarioCreateDto createDto) {
        return new ModelMapper().map(createDto, Usuario.class);
    }

    public static UsuarioResponseDto toDto(Usuario usuario) {
        String role = usuario.getRole().name().substring("ROLE_".length());
        // Classe anônima da ModelMapper, a partir do objeto PropertyMap
        PropertyMap<Usuario, UsuarioResponseDto> props = new PropertyMap<Usuario, UsuarioResponseDto>() {
            @Override
            protected void configure() {
                // A partir do método map, é feito o acesso aos campos presentes em UsuarioResponseDto
                map().setRole(role);
            }  
        };

        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props);
        return mapper.map(usuario, UsuarioResponseDto.class);
    }

    public static List<UsuarioResponseDto> toListDto(List<Usuario> usuarios) {
        return usuarios.stream()
            .map(usuario -> UsuarioMapper.toDto(usuario))
            .collect(Collectors.toList());
    }

}
