package com.banco.digital.domain.exception;

import java.util.UUID;

public class TransferenciaNaoEncontradaException extends RuntimeException {

    public TransferenciaNaoEncontradaException(String numeroConta) {
        super("Transferência não encontrada para o ID: " + numeroConta);
    }
}
