package com.banco.digital.domain.exception;

public class ClienteNaoEncontradoException extends RuntimeException {

    public ClienteNaoEncontradoException(Long id) {
        super("Cliente não encontrado para o ID: " + id);
    }

    public ClienteNaoEncontradoException(String message) {
        super(message);
    }
}
