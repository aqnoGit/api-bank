package com.banco.digital.domain.exception;

public class ContaInativaException extends RuntimeException {

    public ContaInativaException(String numeroConta) {
        super("A conta " + numeroConta + " está inativa e não pode realizar operações.");
    }
}
