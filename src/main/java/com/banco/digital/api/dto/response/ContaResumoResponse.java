package com.banco.digital.api.dto.response;

import com.banco.digital.domain.model.Conta;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resumo de uma conta bancária")
public record ContaResumoResponse(

        @Schema(description = "ID único da conta")
        Long id,

        @Schema(description = "Número da conta", example = "0001-1")
        String numeroConta,

        @Schema(description = "Saldo atual", example = "1500.00")
        BigDecimal saldo,

        @Schema(description = "Indica se a conta está ativa")
        boolean ativa
) {
    public static ContaResumoResponse from(Conta conta) {
        return new ContaResumoResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getSaldo(),
                conta.isAtiva()
        );
    }
}
