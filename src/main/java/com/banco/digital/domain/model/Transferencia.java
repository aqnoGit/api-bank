package com.banco.digital.domain.model;

import com.banco.digital.domain.model.enums.StatusTransferencia;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // chave primária técnica

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_origem_id", nullable = false)
    private Conta contaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_destino_id", nullable = false)
    private Conta contaDestino;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusTransferencia status = StatusTransferencia.PENDENTE;

    @Column(length = 500)
    private String descricao;

    @Column(name = "mensagem_erro", length = 1000)
    private String mensagemErro;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public void concluir() {
        this.status = StatusTransferencia.CONCLUIDA;
        this.completedAt = LocalDateTime.now();
    }

    public void falhar(String motivo) {
        this.status = StatusTransferencia.FALHA;
        this.mensagemErro = motivo;
        this.completedAt = LocalDateTime.now();
    }
}