package com.minhapi.parkapi.web.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhapi.parkapi.entity.Cliente;
import com.minhapi.parkapi.jwt.JwtUserDetails;
import com.minhapi.parkapi.repository.projection.ClienteProjection;
import com.minhapi.parkapi.service.ClienteService;
import com.minhapi.parkapi.service.UsuarioService;

import com.minhapi.parkapi.web.dto.ClienteCreateDto;
import com.minhapi.parkapi.web.dto.ClienteResponseDto;
import com.minhapi.parkapi.web.dto.PageableDto;
import com.minhapi.parkapi.web.dto.mapper.ClienteMapper;
import com.minhapi.parkapi.web.dto.mapper.PageableMapper;
import com.minhapi.parkapi.web.exception.ErrorMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Clientes", description = "Contém todas as operações para os recursos de criação, edicão e listagem de cliente")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    
    @Operation(summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDto.class))),
                @ApiResponse(responseCode = "409", description = "CPF já cadastrado no sistema",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "422", description = "Recurso não processado por entrada de dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> create(@RequestBody @Valid ClienteCreateDto dto, @AuthenticationPrincipal JwtUserDetails userDetails) {
        Cliente cliente = ClienteMapper.toCliente(dto);
        cliente.setUsuario(usuarioService.buscarPorId(userDetails.getId()));
        clienteService.salvar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteMapper.toDto(cliente));
    }
    
    @Operation(summary = "Recuperar um cliente pelo id", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "200", description = "Recurso retornado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))) 
        })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }
    
    @Operation(summary = "Recurso para listar todos os clientes", description = "Requisição exige um Bearer Token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                @Parameter(name = "page",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                description = "Representa a pagina retornada"),
                @Parameter(name = "size",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                description = "Representa a pagina retornada"),
                @Parameter(name = "sort", hidden = true,
                content = @Content(schema = @Schema(type = "string", defaultValue = "id,asc")),
                description = "Representa a pagina retornada"),
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Recurso retornado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDto.class))),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
        Page<ClienteProjection> clientes = clienteService.buscarPorTodos(pageable);
        return ResponseEntity.ok(PageableMapper.toDto(clientes));
    }

    @Operation(summary = "Recuperar detalhes do cliente", description = "Requisição exige um Bearer Token. Acesso restrito a CLIENTE",
    security = @SecurityRequirement(name = "security"),
    responses = {
        @ApiResponse(responseCode = "200", description = "Recurso retornado com sucesso",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Recurso permitido ao perfil de ADMIN",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))) 
})
    @GetMapping("/detalhes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> getDetalhes(@AuthenticationPrincipal JwtUserDetails userDetails) {
        Cliente cliente = clienteService.buscarPorUsuarioId(userDetails.getId());
        return ResponseEntity.ok(ClienteMapper.toDto(cliente));
    }

}