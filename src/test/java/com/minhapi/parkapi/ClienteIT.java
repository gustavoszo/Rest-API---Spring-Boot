package com.minhapi.parkapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.minhapi.parkapi.web.dto.ClienteCreateDto;
import com.minhapi.parkapi.web.dto.ClienteResponseDto;
import com.minhapi.parkapi.web.dto.PageableDto;
import com.minhapi.parkapi.web.exception.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clientes/clientes-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clientes/clientes-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClienteIT {

    @Autowired
    public WebTestClient testClient;

    @Test
    public void createCliente_comCamposValidos_RetornarClienteDtoComStatus201() {
        ClienteResponseDto responseBody = testClient
            .post()
            .uri("/api/v1/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bia@email.com", "123456"))
            .bodyValue(new ClienteCreateDto("Bia Andrade", "67506728095"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isCreated()
            .expectBody(ClienteResponseDto.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getNome()).isEqualTo("Bia Andrade");
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("67506728095");
    }

    @Test
    public void createCliente_comCpfExistente_RetornarErrorMessageComStatus409() {
        ErrorMessage responseBody = testClient
            .post()
            .uri("/api/v1/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bia@email.com", "123456"))
            .bodyValue(new ClienteCreateDto("Bia Andrade", "49344061823"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isEqualTo(409)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createCliente_comCamposInvalidos_RetornarErrorMessageComStatus422() {
        ErrorMessage responseBody = testClient
            .post()
            .uri("/api/v1/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bia@email.com", "123456"))
            .bodyValue(new ClienteCreateDto("Bia Andrade", "4934406182"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
            .post()
            .uri("/api/v1/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "bia@email.com", "123456"))
            .bodyValue(new ClienteCreateDto("Bia", "493440618233"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isEqualTo(422)
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createCliente_comCamposInvalidos_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
            .post()
            .uri("/api/v1/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
            .bodyValue(new ClienteCreateDto("Teo Rodrigues", "49344061823"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void getCliente_comIdEpermissaoValidos_RetornarClienteResponseDtoComStatus200() {
        ClienteResponseDto responseBody = testClient
            .get()
            .uri("/api/v1/clientes/10")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isOk()
            .expectBody(ClienteResponseDto.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void getCliente_comIdInexistente_RetornarErrorMessageComStatus404() {
        ErrorMessage responseBody = testClient
            .get()
            .uri("/api/v1/clientes/15")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isNotFound()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void getCliente_comRoleCLIENTE_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
            .get()
            .uri("/api/v1/clientes/10")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "arnold@email.com", "123456"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(40);
    }

    @Test
    public void getAll_ComPaginacaoPeloAdmin_RetornarClientesComStatus200() {
        PageableDto responseBody = testClient
            .get()
            .uri("/api/v1/clientes")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = testClient
            .get()
            .uri("/api/v1/clientes?size=1&page=1")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isOk()
            .expectBody(PageableDto.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void getDetalhes_comRoleCLIENTE_RetornaClienteComStatus200() {
        ClienteResponseDto responseBody = testClient
            .get()
            .uri("/api/v1/clientes/detalhes")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "arnold@email.com", "123456"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isOk()
            .expectBody(ClienteResponseDto.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getNome()).isEqualTo("Arnold Pro");
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("49344061823");
        org.assertj.core.api.Assertions.assertThat(responseBody.getNome()).isEqualTo("Arnold Pro");
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void getDetalhes_comRoleADMIN_RetornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
            .get()
            .uri("/api/v1/clientes/detalhes")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
