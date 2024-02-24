package com.minhapi.parkapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhapi.parkapi.entity.Vaga;
import com.minhapi.parkapi.entity.Vaga.StatusVaga;
import com.minhapi.parkapi.exception.CodigoUniqueViolationException;
import com.minhapi.parkapi.exception.EntityNotFoundException;
import com.minhapi.parkapi.repository.VagaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VagaService {
    
    private final VagaRepository vagaRepository;   
    private StatusVaga LIVRE = StatusVaga.LIVRE;

    @Transactional
    public Vaga salvar(Vaga vaga) {
        try {
            return vagaRepository.save(vaga);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new CodigoUniqueViolationException(String.format("Vaga com código '%s' já foi cadastrada", vaga.getCodigo()));
        }
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorCodigo(String codigo) {
        return vagaRepository.findByCodigo(codigo).orElseThrow(
            () -> new EntityNotFoundException(String.format("Vaga com código '%s' não encontrada", codigo))
            );
        }
        
    @Transactional(readOnly = true)
    public Vaga buscarPorVagaLivre() {
        return vagaRepository.findFirstByStatus(LIVRE).orElseThrow(
            () -> new EntityNotFoundException("Não foi encontrada vaga livre")
        );
    }

}
