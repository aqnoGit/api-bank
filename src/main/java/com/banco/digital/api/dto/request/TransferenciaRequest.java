package com.banco.digital.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados para realizar uma transferência entre contas")
public record TransferenciaRequest(

    @Schema(description = "Número da conta de origem", example = "12345-6")
    @NotNull(message = "A conta de origem é obrigatória")
    String numeroContaOrigem,

    @Schema(description = "Número da conta de destino", example = "65432-1")
    @NotNull(message = "A conta de destino é obrigatória")
    String numeroContaDestino,

    @Schema(description = "Valor a transferir em reais", example = "250.00")
    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor da transferência deve ser maior que zero")
    BigDecimal valor,

    @Schema(description = "Descrição da transferência", example = "Pagamento de aluguel")
    String descricao
) {}
