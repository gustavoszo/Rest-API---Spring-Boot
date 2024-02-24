package com.minhapi.parkapi.jwt;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.minhapi.parkapi.entity.Usuario;

// É necessário o objeto que extende a interface User, que é um objeto do tipo UserDetails. É esse o objeto que o Spring utiliza para armazenar as informações do usuário logado
public class JwtUserDetails extends User {

    private Usuario usuario;

    public JwtUserDetails(Usuario usuario) {
        super(usuario.getUsername(), usuario.getPassword(), AuthorityUtils.createAuthorityList(usuario.getRole().name()));
        this.usuario = usuario;
    }

    public Long getId() {
        return this.usuario.getId();
    }

    public String getRole() {
        return this.usuario.getRole().name();
    }
    
}
