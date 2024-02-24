package com.minhapi.parkapi.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.minhapi.parkapi.entity.Usuario;
import com.minhapi.parkapi.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        return new JwtUserDetails(usuario);
    }  

    public JwtToken getTokenAuthenticated(String username) {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        return JwtUtils.createToken(username, usuario.getRole().name().substring("ROLE_".length()));
    }

}