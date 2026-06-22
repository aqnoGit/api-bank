package com.banco.digital.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastro de um novo cliente")
public record CadastroClienteRequest(

        @Schema(description = "Nome completo do cliente", example = "João da Silva")
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @Schema(description = "CPF do cliente (somente números ou formatado)", example = "123.456.789-00")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11}",
                 message = "CPF inválido. Use o formato 000.000.000-00 ou apenas os 11 dígitos")
        String cpf,

        @Schema(description = "E-mail do cliente", example = "joao.silva@email.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(description = "Saldo inicial da conta em reais", example = "1000.00")
        @NotNull(message = "O saldo inicial é obrigatório")
        @DecimalMin(value = "0.00", inclusive = true, message = "O saldo inicial não pode ser negativo")
        BigDecimal saldoInicial
) {}
