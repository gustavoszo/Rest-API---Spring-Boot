package com.minhapi.parkapi;

import java.util.function.Consumer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.minhapi.parkapi.jwt.JwtToken;
import com.minhapi.parkapi.web.dto.UserLoginDto;

public class JwtAuthentication {
    
    public static Consumer<HttpHeaders> getTokenAuthorization(WebTestClient client, String username, String password) {
        String token = client
                .post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserLoginDto(username, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody().getToken();
        
        return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

}
