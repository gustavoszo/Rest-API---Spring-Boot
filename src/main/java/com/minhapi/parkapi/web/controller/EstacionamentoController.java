package com.minhapi.parkapi.web.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.minhapi.parkapi.entity.ClienteVaga;
import com.minhapi.parkapi.jwt.JwtUserDetails;
import com.minhapi.parkapi.repository.projection.ClienteVagaProjection;
import com.minhapi.parkapi.service.ClienteVagaService;
import com.minhapi.parkapi.service.EstacionamentoService;
import com.minhapi.parkapi.web.dto.ClienteResponseDto;
import com.minhapi.parkapi.web.dto.EstacionamentoCreateDto;
import com.minhapi.parkapi.web.dto.EstacionamentoResponseDto;
import com.minhapi.parkapi.web.dto.PageableDto;
import com.minhapi.parkapi.web.dto.mapper.EstacionamentoMapper;
import com.minhapi.parkapi.web.dto.mapper.PageableMapper;
import com.minhapi.parkapi.web.exception.ErrorMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Estacionamentos", description = "Operações de registro de entrada e saida de um veículo no estacionamento")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/estacionamentos")
public class EstacionamentoController {
    
    private final EstacionamentoService estacionamentoService;
    private final ClienteVagaService clienteVagaService;

    @Operation(summary = "Operação de check-in", description = "Recurso para dar entrada de um veículo no estacionamento. Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                headers = @Header(name = HttpHeaders.LOCATION, description = "URL de acesso ao recurso criado"),
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Causas possiveis: <br/>" +
                "- CPF do cliente não cadastrado no sistema <br/>" +
                "- Nenhuma vaga livre foi localizada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "422", description = "Recurso não processado por entrada de dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkIn(@Valid @RequestBody EstacionamentoCreateDto dto) {
        ClienteVaga clienteVaga = estacionamentoService.checkIn(EstacionamentoMapper.toClienteVaga(dto));
        EstacionamentoResponseDto responseDto = EstacionamentoMapper.toDto(clienteVaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{recibo}")
                .buildAndExpand(clienteVaga.getRecibo())
                .toUri();
        return ResponseEntity.created(location).body(responseDto);
    }

    @Operation(summary = "Operação de recuperar check-in", description = "Recurso para para recuperar o check-in. Requisição exige uso de um bearer token",
        security = @SecurityRequirement(name = "security"),
        parameters = @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do recibo gerado pelo check-in"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Recurso retornado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Recibo não cadastrado no sistema",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
        })
    @GetMapping("/check-in/{recibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EstacionamentoResponseDto> getByRecibo(@PathVariable String recibo) {
        ClienteVaga clienteVaga = clienteVagaService.buscarPorRecibo(recibo);
        return ResponseEntity.ok(EstacionamentoMapper.toDto(clienteVaga));
    }

    @Operation(summary = "Operação de check-out", description = "Recurso para dar saida de um veículo do estacionamento. Requisição exige uso de um bearer token. Acesso restrito a ADMIN",
        security = @SecurityRequirement(name = "security"),
        parameters = @Parameter(in = ParameterIn.PATH, name = "recibo", description = "Número do recibo gerado pelo check-in"),
        responses = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Causas possiveis: <br/>" +
            "- Recibo não cadastrado no sistema <br/>" +
            "- Veículo já passou pelo check-out",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @PutMapping("/check-out/{recibo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> chekOut(@PathVariable String recibo) {
        ClienteVaga clienteVaga = estacionamentoService.checkOut(recibo);
        return ResponseEntity.ok(EstacionamentoMapper.toDto(clienteVaga));
    }

    @Operation(summary = "Localizar os registros de estacionamentos do cliente por CPF", description = "Localizar os registros de estacionamentos do cliente por CPF. Requisição exige um Bearer Token. Acesso restrito a ADMIN",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                @Parameter(name = "cpf",
                content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                description = "N° do CPF referente ao cliente a ser consultado"),
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
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAllByCpf(@PathVariable String cpf, @Parameter(hidden = true) @PageableDefault(size = 5, sort = "dataEntrada") Pageable pageable) {
        Page<ClienteVagaProjection> projection = clienteVagaService.buscarPorCpf(cpf, pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Localizar os registros de estacionamentos do cliente logado", description = "Localizar os registros de estacionamentos do cliente que está autenticado. Requisição exige um Bearer Token. Acesso para CLIENTE",
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
                @ApiResponse(responseCode = "403", description = "Recurso para perfil de CLIENTE",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @GetMapping()
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PageableDto> getAllEstacionamentosDoCliente(@AuthenticationPrincipal JwtUserDetails userDetails , @Parameter(hidden = true) @PageableDefault(size = 5, sort = "dataEntrada") Pageable pageable) {
        Page<ClienteVagaProjection> projection = clienteVagaService.buscarTodosPorUsuarioId(userDetails.getId(), pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);
    }
}