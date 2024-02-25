package com.minhapi.parkapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.minhapi.parkapi.web.dto.EstacionamentoCreateDto;
import com.minhapi.parkapi.web.dto.PageableDto;
import com.minhapi.parkapi.web.exception.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/estacionamentos/estacionamentos-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/estacionamentos/estacionamentos-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EstacionamentoIT {
    
    @Autowired
    WebTestClient testClient;

    @Test
    public void createCheckin_ComDadosValidos_RetornarCreatedAndLocation() {
        EstacionamentoCreateDto dto = EstacionamentoCreateDto.builder()
            .placa("WER-1111").marca("Fiat").modelo("Palio 1.0")
            .cor("AZUL").clienteCpf("09191773016")
            .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .bodyValue(dto)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists(HttpHeaders.LOCATION)
            .expectBody()
            .jsonPath("placa").isEqualTo("WER-1111")
            .jsonPath("marca").isEqualTo("Fiat")
            .jsonPath("modelo").isEqualTo("Palio 1.0")
            .jsonPath("cor").isEqualTo("AZUL")
            .jsonPath("clienteCpf").isEqualTo("09191773016")
            .jsonPath("recibo").exists()
            .jsonPath("dataEntrada").exists()
            .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void createCheckin_ComDadosInvalidos_RetornarErrorMessageComStatus422() {
        EstacionamentoCreateDto dto = EstacionamentoCreateDto.builder()
            .placa("").marca("").modelo("")
            .cor("").clienteCpf("")
            .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .bodyValue(dto)
            .exchange()
            .expectStatus().isEqualTo(422)
            .expectBody()
            .jsonPath("status").isEqualTo(422)
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("post");
    }

    @Test
    public void createCheckin_ComCliente_RetornarErrorMessageComStatus403() {
        EstacionamentoCreateDto dto = EstacionamentoCreateDto.builder()
        .placa("WER-1111").marca("Fiat").modelo("Palio 1.0")
        .cor("AZUL").clienteCpf("09191773016")
        .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bob@email.com", "123456"))
            .bodyValue(dto)
            .exchange()
            .expectStatus().isEqualTo(403)
            .expectBody()
            .jsonPath("status").isEqualTo(403)
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("post");
    }

    @Test
    public void createCheckin_ComCpfInexistente_RetornarErrorMessageComStatus404() {
        EstacionamentoCreateDto dto = EstacionamentoCreateDto.builder()
        .placa("WER-1111").marca("Fiat").modelo("Palio 1.0")
        .cor("AZUL").clienteCpf("09191773000")
        .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo(404)
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("post");
    }

    @Sql(scripts = "/sql/estacionamentos/estacionamentos-insert-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/estacionamentos/estacionamentos-delete-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void createCheckin_SemVagaDisponivel_RetornarErrorMessageComStatus404() {
        EstacionamentoCreateDto dto = EstacionamentoCreateDto.builder()
        .placa("WER-1111").marca("Fiat").modelo("Palio 1.0")
        .cor("AZUL").clienteCpf("09191773000")
        .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo(404)
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
            .jsonPath("method").isEqualTo("post");
    }

    @Test
    public void getByRecibo_ComReciboValido_RetornarEstacionamentoStatus200() {
        testClient
            .get()
            .uri("/api/v1/estacionamentos/check-in/20230313-101300")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("placa").isEqualTo("FIT-1020")
            .jsonPath("marca").isEqualTo("FIAT")
            .jsonPath("modelo").isEqualTo("PALIO")
            .jsonPath("cor").isEqualTo("VERDE")
            .jsonPath("recibo").isEqualTo("20230313-101300")
            .jsonPath("dataEntrada").exists()
            .jsonPath("recibo").isEqualTo("20230313-101300")
            .jsonPath("clienteCpf").isEqualTo("98401203015")
            .jsonPath("vagaCodigo").isEqualTo("A-01");

        testClient
            .get()
            .uri("/api/v1/estacionamentos/check-in/20230313-101300")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bob@email.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("placa").isEqualTo("FIT-1020")
            .jsonPath("marca").isEqualTo("FIAT")
            .jsonPath("modelo").isEqualTo("PALIO")
            .jsonPath("cor").isEqualTo("VERDE")
            .jsonPath("recibo").isEqualTo("20230313-101300")
            .jsonPath("dataEntrada").isEqualTo("20230313-101300")
            .jsonPath("recibo").isEqualTo("20230313-101300")
            .jsonPath("clienteCpf").isEqualTo("98401203015")
            .jsonPath("vagaCodigo").isEqualTo("A-01");
    }

    @Test
    public void getByRecibo_ComReciboInvalido_RetornarStatus404() {
        testClient
            .get()
            .uri("/api/v1/estacionamentos/check-in/20230313-000000")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo(404)
            .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in/20230313-000000")
            .jsonPath("method").isEqualTo("get");
    }

    @Test
    public void checkout_ComReciboValido_RetornarStatus200() {
        testClient
            .put()
            .uri("/api/v1/estacionamentos/check-in/20230313-000000")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("placa").isEqualTo("FIT-1020")
            .jsonPath("marca").isEqualTo("FIAT")
            .jsonPath("modelo").isEqualTo("PALIO")
            .jsonPath("cor").isEqualTo("VERDE")
            .jsonPath("recibo").isEqualTo("20230313-101300")
            .jsonPath("dataEntrada").isEqualTo("20230313-101300")
            .jsonPath("recibo").isEqualTo("20230313-101300")
            .jsonPath("clienteCpf").isEqualTo("98401203015")
            .jsonPath("vagaCodigo").isEqualTo("A-01")
            .jsonPath("dataSaida").exists()
            .jsonPath("valor").exists()
            .jsonPath("valor").exists();
    }

    @Test
    public void checkout_ComCLIENTE_RetornarStatus403() {
        testClient
            .put()
            .uri("/api/v1/estacionamentos/check-in/20230313-000000")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bob@email.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody()
            .jsonPath("status").isEqualTo(403)
            .jsonPath("method").isEqualTo("put")
            .jsonPath("uri").isEqualTo("/api/v1/estacionamentos/check-in/20230313-000000");
    }

    @Test
    public void checkout_ComReciboInvalido_RetornarStatus404() {
        testClient
            .put()
            .uri("/api/v1/estacionamentos/check-in/20230313-000000")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("status").isEqualTo(404)
            .jsonPath("method").isEqualTo("put")
            .jsonPath("uri").isEqualTo("/api/v1/estacionamentos/check-in/20230313-000000");
    }

    @Test
    public void buscarEstacionamentosPorCpf_ComCpfValido_RetornarStatus200() {
        PageableDto responseBody = testClient
            .get()
            .uri("/api/v1/estacionamentos/cpf/98401203015")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);
    }

    @Test
    public void buscarEstacionamentosPorCpf_ComCLIENTE_RetornarStatus403() {
        ErrorMessage responseBody = testClient
            .get()
            .uri("/api/v1/estacionamentos/cpf/98401203015")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bob@email.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
        org.assertj.core.api.Assertions.assertThat(responseBody.getMethod()).isEqualTo("get");
        org.assertj.core.api.Assertions.assertThat(responseBody.getPath()).isEqualTo("/api/v1/estacionamentos/cpf/98401203015");
    }

    
    @Test
    public void buscarEstacionamentosPorClienteLogado_RetornarStatus200() {
        PageableDto responseBody = testClient
            .get()
            .uri("/api/v1/estacionamentos")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bob@email.com", "123456"))
            .exchange()
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalElements()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);
    }
    
    @Test
    public void buscarEstacionamentosPorClienteLogado_ComADMIN_RetornarStatus403() {
        ErrorMessage responseBody = testClient
            .get()
            .uri("/api/v1/estacionamentos")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "ana@email.com", "123456"))
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
        org.assertj.core.api.Assertions.assertThat(responseBody.getMethod()).isEqualTo("get");
    }
}