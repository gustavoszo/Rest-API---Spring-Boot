package com.minhapi.parkapi.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minhapi.parkapi.entity.Cliente;
import com.minhapi.parkapi.entity.ClienteVaga;
import com.minhapi.parkapi.entity.Vaga;
import com.minhapi.parkapi.util.EstacionamentoUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EstacionamentoService {
    
    private final ClienteVagaService clienteVagaService;
    private final ClienteService clienteService;
    private final VagaService vagaService;

    @Transactional
    public ClienteVaga checkIn(ClienteVaga clienteVaga) {
        Cliente cliente = clienteService.buscarPorCpf(clienteVaga.getCliente().getCpf());
        clienteVaga.setCliente(cliente);

        Vaga vaga = vagaService.buscarPorVagaLivre();
        vaga.setStatus(Vaga.StatusVaga.OCUPADA);

        clienteVaga.setVaga(vaga);
        clienteVaga.setRecibo(EstacionamentoUtils.gerarRecibo());
        clienteVaga.setDataEntrada(LocalDateTime.now());

        return clienteVagaService.salvar(clienteVaga);
    }

    @Transactional
    public ClienteVaga checkOut(String recibo) {
        ClienteVaga clienteVaga = clienteVagaService.buscarPorRecibo(recibo);
        clienteVaga.setDataSaida(LocalDateTime.now());
        clienteVaga.setValor(EstacionamentoUtils.calcularCusto(clienteVaga.getDataEntrada(), clienteVaga.getDataSaida()));

        long qt = clienteVagaService.getTotalDeVezesEstacionamentoCompleto(clienteVaga.getCliente().getCpf());
        clienteVaga.setDesconto(EstacionamentoUtils.calcularDesconto(clienteVaga.getValor(), qt));
        clienteVaga.getVaga().setStatus(Vaga.StatusVaga.LIVRE);
        return clienteVagaService.salvar(clienteVaga);
    }

}