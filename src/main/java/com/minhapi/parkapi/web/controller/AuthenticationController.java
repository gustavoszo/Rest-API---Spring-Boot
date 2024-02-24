package com.minhapi.parkapi.web.controller;

import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhapi.parkapi.jwt.JwtToken;
import com.minhapi.parkapi.jwt.JwtUserDetailsService;
import com.minhapi.parkapi.web.dto.UserLoginDto;
import com.minhapi.parkapi.web.exception.ErrorMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Autenticação", description = "Recurso para proceder a autenticação na API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    private final JwtUserDetailsService detailsService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Autenticar na API", description = "Recurso de autenticação na API",
            responses = {
                @ApiResponse(responseCode = "200", description = "Token gerado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))),
                @ApiResponse(responseCode = "400", description = "Credenciais inválidas",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "422", description = "Recurso não processado por entrada de dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
        })
    @PostMapping("/auth")
    public ResponseEntity<?> autenticar(@RequestBody @Valid UserLoginDto loginDto, HttpServletRequest request) {
        log.info("Processo de autenticação pelo login " + loginDto.getUsername());
        try {
            // O objeto authenticationToken da classe UsernamePasswordAuthenticationToken, vai procurar se existe no banco esse usuário
            UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()); 

            authenticationManager.authenticate(authenticationToken);

            JwtToken token = detailsService.getTokenAuthenticated(loginDto.getUsername());

            return ResponseEntity.ok().body(token);

        } catch (AuthenticationException e) {
            log.warn("Bad credential for username '{}'", loginDto.getUsername());
        }
        return ResponseEntity.badRequest().body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credenciais inválidas"));
    }
    
}
