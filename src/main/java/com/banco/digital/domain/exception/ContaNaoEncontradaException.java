package com.banco.digital.domain.exception;

public class ContaNaoEncontradaException extends RuntimeException {

    public ContaNaoEncontradaException(String numeroConta) {
        super("Conta não encontrada para o numero: " + numeroConta);
    }
}
