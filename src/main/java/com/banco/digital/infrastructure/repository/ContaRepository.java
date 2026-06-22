package com.banco.digital.infrastructure.repository;

import com.banco.digital.domain.model.Conta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    // Busca contas pelo CPF do cliente
    List<Conta> findByClienteCpf(String cpf);

    // Busca conta pelo número da conta
    Optional<Conta> findByNumeroConta(String numeroConta);

    // Verifica se já existe uma conta com determinado número
    boolean existsByNumeroConta(String numeroConta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Conta c WHERE c.numeroConta = :numeroConta")
    Optional<Conta> findByNumeroContaWithLock(@Param("numeroConta") String numeroConta);
}
