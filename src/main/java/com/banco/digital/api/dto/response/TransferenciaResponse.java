package com.banco.digital.api.dto.response;

import com.banco.digital.domain.model.Transferencia;
import com.banco.digital.domain.model.enums.StatusTransferencia;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de uma transferência realizada")
public record TransferenciaResponse(

        @Schema(description = "ID único da transferência")
        Long id,

        @Schema(description = "ID da conta de origem")
        Long contaOrigemId,

        @Schema(description = "Número da conta de origem")
        String numeroContaOrigem,

        @Schema(description = "Nome do titular da origem")
        String nomeOrigemCliente,

        @Schema(description = "ID da conta de destino")
        Long contaDestinoId,

        @Schema(description = "Número da conta de destino")
        String numeroContaDestino,

        @Schema(description = "Nome do titular do destino")
        String nomeDestinoCliente,

        @Schema(description = "Valor transferido")
        BigDecimal valor,

        @Schema(description = "Status da transferência")
        StatusTransferencia status,

        @Schema(description = "Descrição da transferência")
        String descricao,

        @Schema(description = "Mensagem de erro, se houver falha")
        String mensagemErro,

        @Schema(description = "Data de criação")
        LocalDateTime createdAt,

        @Schema(description = "Data de conclusão")
        LocalDateTime completedAt
) {
    public static TransferenciaResponse from(Transferencia t) {
        return new TransferenciaResponse(
                t.getId(),
                t.getContaOrigem().getId(),
                t.getContaOrigem().getNumeroConta(),
                t.getContaOrigem().getCliente().getNome(),
                t.getContaDestino().getId(),
                t.getContaDestino().getNumeroConta(),
                t.getContaDestino().getCliente().getNome(),
                t.getValor(),
                t.getStatus(),
                t.getDescricao(),
                t.getMensagemErro(),
                t.getCreatedAt(),
                t.getCompletedAt()
        );
    }
}