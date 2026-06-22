package com.banco.digital.application.service;

import com.banco.digital.infrastructure.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gerador de número de conta sequencial e thread-safe.
 * Em produção, usaria uma sequence do banco de dados.
 */
@Component
@RequiredArgsConstructor
public class NumerContaGenerator {

    private final ContaRepository contaRepository;
    private final AtomicInteger sequencial = new AtomicInteger(0);

    public String gerar() {
        long total = contaRepository.count() + sequencial.incrementAndGet();
        return String.format("%04d-1", total);
    }
}
