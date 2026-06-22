package com.banco.digital.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta de erro padrão da API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(

        @Schema(description = "Código HTTP do erro")
        int status,

        @Schema(description = "Mensagem descritiva do erro")
        String mensagem,

        @Schema(description = "Detalhes adicionais de validação")
        List<String> detalhes,

        @Schema(description = "Timestamp do erro")
        LocalDateTime timestamp
) {
    public static ErroResponse of(int status, String mensagem) {
        return new ErroResponse(status, mensagem, null, LocalDateTime.now());
    }

    public static ErroResponse of(int status, String mensagem, List<String> detalhes) {
        return new ErroResponse(status, mensagem, detalhes, LocalDateTime.now());
    }
}
