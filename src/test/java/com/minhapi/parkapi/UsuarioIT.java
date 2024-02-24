package com.minhapi.parkapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.minhapi.parkapi.web.dto.UsuarioCreateDto;
import com.minhapi.parkapi.web.dto.UsuarioResponseDto;
import com.minhapi.parkapi.web.dto.UsuarioSenhaDto;
import com.minhapi.parkapi.web.exception.ErrorMessage;
import java.util.List;

// Faz com que o tomcat seja executado em uma porta de forma randomica. Assim, não executar o tomcat da aplicação e sim um tomcat em ambiente de teste
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) 
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {

    @Autowired
    WebTestClient testClient; // Objeto para trabalhar com teste

    @Test
    public void createUser_comUsernameEpasswordValidos_retornarUsuarioResponseDtoComStatus201() {
        UsuarioResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ben@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

                // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("ben@email.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUser_comUsernameInvalido_retornarErrorMessageComStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("bonr@email", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

                // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUser_comUsernameJaExistente_retornarErrorMessageComStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("teo@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

                // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }
    
    @Test
    public void buscarUsuario_comIdExistente_retornarUsuarioResponseDtoComStatus200() {
        UsuarioResponseDto responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

                // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("teo@email.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");
        
        responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

                // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("bia@email.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");

        responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "bia@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

                // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responseBody.getUsername()).isEqualTo("bia@email.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");

    }
    
    @Test
    public void buscarUsuario_comIdInvalido_retornarErrorMessageComStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/105")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

                // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }
    
    @Test
    public void buscarUsuario_comUsuarioClienteBuscandoPorOutroUsuario_retornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "arnold@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

                // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void alterarSenha_comSenhasValidas_retornarStatus204() {
        testClient
            .patch()
            .uri("/api/v1/usuarios/101")
            .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
            .bodyValue(new UsuarioSenhaDto("123456", "123456", "123456"))
            .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
            .expectStatus().isForbidden()
            .expectBody(ErrorMessage.class)
            .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto
            // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto
    }

    @Test
    public void alterarSenha_comIdUserDiferente_retornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
        .patch()
        .uri("/api/v1/usuarios/101")
        .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
        .bodyValue(new UsuarioSenhaDto("123456", "123456", "123456"))
        .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
        .expectStatus().isForbidden()
        .expectBody(ErrorMessage.class)
        .returnResult().getResponseBody(); // O método getResponseBody() faz com que seja retornado um objeto do tipo UsuarioResponseDto

        // Agora, verificar o retorno da requisicao; o objeto retornado pela requisição

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void alterarSenha_comSenhaIncorreta_retornarErrorMessageComStatus422() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123457", "abcdef", "abcdef"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void alterarSenha_comSenhasDiferentes_retornarErrorMessageComStatus400() {
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "abcdef", "abcdeg"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
                
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void alterarSenha_comCamposInvalidos_retornarErrorMessageComStatus422() {
        
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("12345", "abcdef", "abcdef"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
        
            org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
            org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "abcde", "abcdef"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
        
            org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
            org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "abcdefgf", "abcdef"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
        
            org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
            org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void getAll_retornarListaDeUsuariosComStatus200() {
        List<UsuarioResponseDto> responseBody = testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "teo@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isOk()
                .expectBodyList(UsuarioResponseDto.class)
                .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.size()).isEqualTo(3);
    }

    @Test
    public void getAll_retornarErrorMessageComStatus403() {
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JwtAuthentication.getTokenAuthorization(testClient, "arnold@email.com", "123456"))
                .exchange() // A partir desse método, começa a trabalhar com a parte de resposta, aquilo que é esperado de retorno
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();
        
        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

}
