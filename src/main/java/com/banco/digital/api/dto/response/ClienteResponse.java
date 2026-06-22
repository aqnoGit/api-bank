package com.banco.digital.api.dto.response;

import com.banco.digital.domain.model.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Dados de retorno de um cliente")
public record ClienteResponse(

        @Schema(description = "ID único do cliente")
        Long id,

        @Schema(description = "Nome completo")
        String nome,

        @Schema(description = "CPF formatado")
        String cpf,

        @Schema(description = "E-mail")
        String email,

        @Schema(description = "Contas do cliente")
        List<ContaResumoResponse> contas,

        @Schema(description = "Data de cadastro")
        LocalDateTime createdAt
) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getContas().stream().map(ContaResumoResponse::from).toList(),
                cliente.getCreatedAt()
        );
    }
}
