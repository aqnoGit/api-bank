package com.banco.digital.domain.model;

import com.banco.digital.domain.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Conta - Regras de Domínio")
class ContaTest {

    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = Conta.builder()
                .numeroConta("0001-1")
                .saldo(new BigDecimal("1000.00"))
                .ativa(true)
                .build();
    }

    @Nested
    @DisplayName("Débito")
    class Debito {

        @Test
        @DisplayName("Deve debitar valor válido com sucesso")
        void deveDebitarValorValido() {
            conta.debitar(new BigDecimal("300.00"));
            assertThat(conta.getSaldo()).isEqualByComparingTo("700.00");
        }

        @Test
        @DisplayName("Deve debitar o saldo total exato")
        void deveDebitarSaldoTotal() {
            conta.debitar(new BigDecimal("1000.00"));
            assertThat(conta.getSaldo()).isEqualByComparingTo("0.00");
        }

        @Test
        @DisplayName("Deve lançar SaldoInsuficienteException quando valor excede saldo")
        void deveLancarExcecaoQuandoSaldoInsuficiente() {
            assertThatThrownBy(() -> conta.debitar(new BigDecimal("1500.00")))
                    .isInstanceOf(SaldoInsuficienteException.class)
                    .hasMessageContaining("Saldo insuficiente");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para valor zero")
        void deveLancarExcecaoParaValorZero() {
            assertThatThrownBy(() -> conta.debitar(BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para valor negativo")
        void deveLancarExcecaoParaValorNegativo() {
            assertThatThrownBy(() -> conta.debitar(new BigDecimal("-50.00")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Crédito")
    class Credito {

        @Test
        @DisplayName("Deve creditar valor com sucesso")
        void deveCreditarValor() {
            conta.creditar(new BigDecimal("500.00"));
            assertThat(conta.getSaldo()).isEqualByComparingTo("1500.00");
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para valor zero")
        void deveLancarExcecaoParaValorZero() {
            assertThatThrownBy(() -> conta.creditar(BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException para valor negativo")
        void deveLancarExcecaoParaValorNegativo() {
            assertThatThrownBy(() -> conta.creditar(new BigDecimal("-100.00")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
