package com.banco.digital.application.service;

import com.banco.digital.api.dto.response.ContaResponse;
import com.banco.digital.domain.exception.ContaNaoEncontradaException;
import com.banco.digital.domain.model.Conta;
import com.banco.digital.infrastructure.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    @Transactional(readOnly = true)
    public ContaResponse buscarPorNumeroConta(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(numeroConta));
        return ContaResponse.from(conta);
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> listarPorCliente(String clienteNumeroDocumento) {
        return contaRepository.findByClienteCpf(clienteNumeroDocumento)
                .stream()
                .map(ContaResponse::from)
                .toList();
    }

    @Transactional
    public ContaResponse desativar(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(numeroConta));
        conta.setAtiva(false);
        contaRepository.save(conta);
        log.info("Conta {} desativada.", conta.getNumeroConta());
        return ContaResponse.from(conta);
    }
}
