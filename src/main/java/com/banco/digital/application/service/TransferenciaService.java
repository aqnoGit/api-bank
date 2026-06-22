package com.banco.digital.application.service;

import com.banco.digital.api.dto.request.TransferenciaRequest;
import com.banco.digital.api.dto.response.TransferenciaResponse;
import com.banco.digital.domain.exception.ContaInativaException;
import com.banco.digital.domain.exception.ContaNaoEncontradaException;
import com.banco.digital.domain.exception.TransferenciaInvalidaException;
import com.banco.digital.domain.model.Conta;
import com.banco.digital.domain.model.Transferencia;
import com.banco.digital.infrastructure.notification.NotificacaoService;
import com.banco.digital.infrastructure.repository.ContaRepository;
import com.banco.digital.infrastructure.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferenciaService {

    private final ContaRepository contaRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final NotificacaoService notificacaoService;

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest request) {
        validarMesmasConta(request);

        Conta contaOrigem = contaRepository.findByNumeroConta(request.numeroContaOrigem())
                .orElseThrow(() -> new ContaNaoEncontradaException(request.numeroContaOrigem()));

        Conta contaDestino = contaRepository.findByNumeroConta(request.numeroContaDestino())
                .orElseThrow(() -> new ContaNaoEncontradaException(request.numeroContaDestino()));

        validarContasAtivas(contaOrigem, contaDestino);

        if (contaOrigem.getSaldo().compareTo(request.valor()) < 0) {
            throw new com.banco.digital.domain.exception.SaldoInsuficienteException(contaOrigem.getNumeroConta());
        }

        Transferencia transferencia = Transferencia.builder()
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .valor(request.valor())
                .descricao(request.descricao())
                .build();

        transferencia = transferenciaRepository.save(transferencia);

        contaOrigem.debitar(request.valor());
        contaDestino.creditar(request.valor());

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        transferencia.concluir();
        transferencia = transferenciaRepository.save(transferencia);

        log.info("Transferência concluída. ID: {}, Origem: {}, Destino: {}, Valor: {}",
                transferencia.getId(),
                contaOrigem.getNumeroConta(),
                contaDestino.getNumeroConta(),
                request.valor());

        TransferenciaResponse response = TransferenciaResponse.from(transferencia);

        notificacaoService.notificarTransferencia(
                transferencia.getId(),
                contaOrigem.getId(),
                contaDestino.getId(),
                request.valor(),
                transferencia.getStatus().name(),
                contaOrigem.getCliente().getNome(),
                contaDestino.getCliente().getNome()
        );

        return response;
    }

    @Transactional(readOnly = true)
    public Page<TransferenciaResponse> listarMovimentacoes(String numeroConta, Pageable pageable) {
        contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(numeroConta));

        return transferenciaRepository
                .findByContaNumero(numeroConta, pageable)
                .map(TransferenciaResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TransferenciaResponse> buscarPorNumeroConta(String numeroConta, Pageable pageable) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(numeroConta));

        return transferenciaRepository.findByContaNumero(conta.getNumeroConta(), pageable)
                .map(TransferenciaResponse::from);
    }

    private void validarMesmasConta(TransferenciaRequest request) {
        if (request.numeroContaOrigem().equals(request.numeroContaDestino())) {
            throw new TransferenciaInvalidaException("Conta de origem e destino não podem ser iguais.");
        }
    }

    private void validarContasAtivas(Conta origem, Conta destino) {
        if (!origem.isAtiva()) {
            throw new ContaInativaException(origem.getNumeroConta());
        }
        if (!destino.isAtiva()) {
            throw new ContaInativaException(destino.getNumeroConta());
        }
    }
}
