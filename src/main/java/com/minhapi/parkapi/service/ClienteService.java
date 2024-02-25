package com.minhapi.parkapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhapi.parkapi.entity.Cliente;
import com.minhapi.parkapi.exception.CpfUniqueViolationException;
import com.minhapi.parkapi.exception.EntityNotFoundException;
import com.minhapi.parkapi.repository.ClienteRepository;
import com.minhapi.parkapi.repository.projection.ClienteProjection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClienteService{
    
    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente salvar(Cliente cliente) {
        try {
            return clienteRepository.save(cliente);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(String.format("O CPF '%s' já foi cadastrado", cliente.getCpf()));
        }
        
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
       return clienteRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException(String.format("Cliente id '%s' não encontrado", id))
       );
    }

    @Transactional(readOnly = true)
    public Page<ClienteProjection> buscarPorTodos(Pageable pageable) {
        return clienteRepository.findAllPageable(pageable);    
    }

    public Cliente buscarPorUsuarioId(Long id) {
        // Campo usuario do cliente
        return clienteRepository.findByUsuarioId(id);
    }

    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf).orElseThrow(
            () -> new EntityNotFoundException(String.format("Não foi encontrado o cliente com cpf '%s'", cpf))
        );
    }

}
