package com.banco.digital.domain.model;

import com.banco.digital.domain.exception.SaldoInsuficienteException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // chave primária técnica

    @Column(name = "numero_conta", nullable = false, unique = true)
    private String numeroConta;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Debita o valor da conta, garantindo saldo suficiente.
     */
    public void debitar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor a debitar deve ser positivo.");
        }
        if (this.saldo.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente. Saldo atual: " + this.saldo + ", valor solicitado: " + valor
            );
        }
        this.saldo = this.saldo.subtract(valor);
    }

    /**
     * Credita o valor na conta.
     */
    public void creditar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor a creditar deve ser positivo.");
        }
        this.saldo = this.saldo.add(valor);
    }
}
