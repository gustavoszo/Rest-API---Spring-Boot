package com.minhapi.parkapi.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.minhapi.parkapi.jwt.JwtAuthenticationEntryPoint;
import com.minhapi.parkapi.jwt.JwtAuthorizationFilter;

import org.springframework.http.HttpMethod;

@Configuration
@EnableMethodSecurity
@EnableWebMvc
public class SpringSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        return http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable()) 
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                        antMatcher(HttpMethod.POST, "/api/v1/usuarios"), // Não precisa estar autenticado 
                        antMatcher(HttpMethod.POST, "/api/v1/auth"), 
                        antMatcher("/docs-park.html"), 
                        antMatcher("/docs-park/**"),
                        antMatcher("/swagger-ui.html"),
                        antMatcher("/swagger-ui/**"),
                        antMatcher("/web-jars/**")
                    ).permitAll()
                    .anyRequest().authenticated() // Qualquer outra uri precisa estar autenticado
            ).sessionManagement(
                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Tipo de gerenciamento de sessão
            ).addFilterBefore(
                    jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class
            ).exceptionHandling (ex -> ex
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
            ).build();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { // Gerenciamento de autenticação
        return authenticationConfiguration.getAuthenticationManager();
    }

}