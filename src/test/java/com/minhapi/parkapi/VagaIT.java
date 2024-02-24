package com.minhapi.parkapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.minhapi.parkapi.web.dto.VagaCreateDto;
import com.minhapi.parkapi.web.dto.VagaResponseDto;
import com.minhapi.parkapi.web.exception.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/vagas/vagas-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/vagas/vagas-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class VagaIT {
    
    @Autowired
    private WebTestClient testClient;

    @Test
    public void createVaga_ComDadosValidos_RetornarLocationComStatus201() {
        testClient
        .post()
        .uri("/api/v1/vagas")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
        .bodyValue(new VagaCreateDto("A-05", "LIVRE"))
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().exists(HttpHeaders.LOCATION);
    }
    
    @Test
    public void createVaga_ComCodigoJaExistente_RetornarErrorMessageComStatus409() {
        ErrorMessage responseBody = testClient
        .post()
        .uri("/api/v1/vagas")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
        .bodyValue(new VagaCreateDto("A-01", "LIVRE"))
        .exchange()
        .expectStatus().isEqualTo(409)
        .expectBody(ErrorMessage.class)
        .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }
    
    @Test
    public void createVaga_ComCLIENTE_RetornarErrorMessageComStatus422() {
        ErrorMessage responseBody = testClient
        .post()
        .uri("/api/v1/vagas")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(JwtAuthentication.getTokenAuthorization(testClient, "arnold@email.com", "123456"))
        .bodyValue(new VagaCreateDto("A-0", "LIVRE"))
        .exchange()
        .expectStatus().isEqualTo(422)
        .expectBody(ErrorMessage.class)
        .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }
 
    @Test
    public void createVaga_ComCLIENTE_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
        .post()
        .uri("/api/v1/vagas/")
        .headers(JwtAuthentication.getTokenAuthorization(testClient, "arnold@email.com", "123456"))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new VagaCreateDto("A-05", "LIVRE"))
        .exchange()
        .expectStatus().isForbidden()
        .expectBody(ErrorMessage.class)
        .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
    
    @Test
    public void buscarVaga_RetornarVagaResponseDtoComStatus200() {
        VagaResponseDto responseBody = testClient
        .get()
        .uri("/api/v1/vagas/A-01")
        .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
        .exchange()
        .expectStatus().isOk()
        .expectBody(VagaResponseDto.class)
        .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getCodigo()).isEqualTo("A-01");
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo("LIVRE");
    }
    
    @Test
    public void buscarVaga_ComCLIENTE_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
        .get()
        .uri("/api/v1/vagas/A-01")
        .headers(JwtAuthentication.getTokenAuthorization(testClient, "arnold@email.com", "123456"))
        .exchange()
        .expectStatus().isForbidden()
        .expectBody(ErrorMessage.class)
        .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}   
