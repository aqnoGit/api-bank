package com.banco.digital.infrastructure.repository;

import com.banco.digital.domain.model.Transferencia;
import com.banco.digital.domain.model.enums.StatusTransferencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

    /**
     * Retorna todas as transferências (enviadas ou recebidas) de uma conta pelo número da conta.
     * Usa JOIN FETCH para evitar N+1 queries e lazy-loading fora da sessão.
     */
    @Query(
            value = """
            SELECT t FROM Transferencia t
            JOIN FETCH t.contaOrigem co
            JOIN FETCH co.cliente
            JOIN FETCH t.contaDestino cd
            JOIN FETCH cd.cliente
            WHERE co.numeroConta = :numeroConta OR cd.numeroConta = :numeroConta
            """,
            countQuery = """
            SELECT COUNT(t) FROM Transferencia t
            WHERE t.contaOrigem.numeroConta = :numeroConta OR t.contaDestino.numeroConta = :numeroConta
            """
    )
    Page<Transferencia> findByContaNumero(@Param("numeroConta") String numeroConta, Pageable pageable);

    Page<Transferencia> findByContaOrigemNumeroContaOrderByCreatedAtDesc(String numeroConta, Pageable pageable);

    Page<Transferencia> findByContaDestinoNumeroContaOrderByCreatedAtDesc(String numeroConta, Pageable pageable);

    Page<Transferencia> findByStatusOrderByCreatedAtDesc(StatusTransferencia status, Pageable pageable);
}
