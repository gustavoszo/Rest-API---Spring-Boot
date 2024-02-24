package com.minhapi.parkapi.web.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.minhapi.parkapi.entity.Vaga;
import com.minhapi.parkapi.service.VagaService;
import com.minhapi.parkapi.web.dto.VagaCreateDto;
import com.minhapi.parkapi.web.dto.VagaResponseDto;
import com.minhapi.parkapi.web.dto.mapper.VagaMapper;
import com.minhapi.parkapi.web.exception.ErrorMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Vagas", description = "Contém todas as operações relativas aos recursos de vagas")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vagas")
public class VagaController {
    
    private final VagaService vagaService;

    @Operation(summary = "Criar uma nova vaga", description = "Requisição exige um Bearer token. Acesso restrito ao perfil de ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado")),
                @ApiResponse(responseCode = "409", description = "Código de vaga já cadastrada no sistema",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "422", description = "Recurso não processado por entrada de dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para perfil de ADMIN",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody VagaCreateDto dto) {
        Vaga vaga = vagaService.salvar(VagaMapper.toVaga(dto));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{codigo}")
                .buildAndExpand(vaga.getCodigo())
                .toUri();
        return ResponseEntity.created(location).build();
    }
     
    @Operation(summary = "Recuperar uma vaga pelo código", description = "Requisição exige um Bearer token. Acesso restrito ao perfil de ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "200", description = "Recurso retornado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = VagaResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Vaga mão encontrada no sistema",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido para perfil de CLIENTE",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para perfil de ADMIN",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseDto> getByCodigo(@Valid @PathVariable String codigo) {
        Vaga vaga = vagaService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(VagaMapper.toDto(vaga));
    }

}
