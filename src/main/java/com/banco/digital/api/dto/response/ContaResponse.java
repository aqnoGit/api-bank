package com.banco.digital.api.dto.response;

import com.banco.digital.domain.model.Conta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados completos de uma conta bancária")
public record ContaResponse(

        @Schema(description = "ID único da conta")
        Long id,

        @Schema(description = "Número da conta", example = "0001-1")
        String numeroConta,

        @Schema(description = "Saldo atual", example = "1500.00")
        BigDecimal saldo,

        @Schema(description = "Indica se a conta está ativa")
        boolean ativa,

        @Schema(description = "Nome do titular")
        String nomeCliente,

        @Schema(description = "ID do titular")
        Long clienteId,

        @Schema(description = "Data de criação")
        LocalDateTime createdAt
) {
    public static ContaResponse from(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getSaldo(),
                conta.isAtiva(),
                conta.getCliente().getNome(),
                conta.getCliente().getId(),
                conta.getCreatedAt()
        );
    }
}
