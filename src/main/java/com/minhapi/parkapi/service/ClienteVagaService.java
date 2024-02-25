package com.minhapi.parkapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhapi.parkapi.entity.ClienteVaga;
import com.minhapi.parkapi.exception.EntityNotFoundException;
import com.minhapi.parkapi.repository.ClienteVagaRepository;
import com.minhapi.parkapi.repository.projection.ClienteVagaProjection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ClienteVagaService {
    
    private final ClienteVagaRepository repository;

    @Transactional
    public ClienteVaga salvar(ClienteVaga clienteVaga) {
        return repository.save(clienteVaga);
    } 

    @Transactional(readOnly = true)
    public ClienteVaga buscarPorRecibo(String recibo) {
        return repository.findByReciboAndDataSaidaIsNull(recibo).orElseThrow(
            () -> new EntityNotFoundException(String.format("Recibo '%s' não encontrado ou check-out já realizado", recibo))
        );
    } 

    @Transactional(readOnly = true)
    public Long getTotalDeVezesEstacionamentoCompleto(String cpf) {
        // count é uma palavra chave para a contagem
        return repository.countByClienteCpfAndDataSaidaIsNotNull(cpf);
    }

    public Page<ClienteVagaProjection> buscarPorCpf(String cpf, Pageable pageable) {
        return repository.findAllByClienteCpf(cpf, pageable);
    }

    public Page<ClienteVagaProjection> buscarTodosPorUsuarioId(Long id, Pageable pageable) {
        // Campo Cliente do ClienteVaga
        return repository.findAllByClienteUsuarioId(id);
    } 

}
