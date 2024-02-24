package com.minhapi.parkapi.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhapi.parkapi.repository.UsuarioRepository;
import com.minhapi.parkapi.entity.Usuario;
import com.minhapi.parkapi.exception.EntityNotFoundException;
import com.minhapi.parkapi.exception.ErrorPasswordException;
import com.minhapi.parkapi.exception.UsernameUniqueViolationException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Método da interface JpaRepository
    @Transactional // Anotação para o spring tomar conta da transação, ele vai abrir, gerenciar e fechar a transação
    public Usuario salvar(Usuario usuario) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuarioRepository.save(usuario);
        } catch(org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("E-mail '%s' já cadastrado", usuario.getUsername())); // String.format é parecido com um printf
        }
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException(String.format("Usuário id= %s não encontrado", id))
       );

    }

    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmacaoSenha) {
        Usuario user = buscarPorId(id);

        if (! passwordEncoder.matches(senhaAtual, user.getPassword())) {
            throw new ErrorPasswordException("Sua senha não confere");
        }

        if (! senhaAtual.equals(confirmacaoSenha)) {
            throw new ErrorPasswordException("Nova senha e confirmação de senha diferentes");
        }
        // O hibernate já vai enviar a atualização para o banco, pois ele tem o controle do objeto enquanto a requisição é feita
        user.setPassword(passwordEncoder.encode(confirmacaoSenha));
        return user;
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(
            () -> new EntityNotFoundException(String.format("Usuário '%s' não encontrado", username))
       );
    }

}
