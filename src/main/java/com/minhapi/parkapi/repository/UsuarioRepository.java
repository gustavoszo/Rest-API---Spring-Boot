package com.minhapi.parkapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhapi.parkapi.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

   Optional<Usuario> findByUsername(String username);
}