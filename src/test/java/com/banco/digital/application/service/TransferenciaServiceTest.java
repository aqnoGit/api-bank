package com.banco.digital.application.service;

import com.banco.digital.api.dto.request.TransferenciaRequest;
import com.banco.digital.api.dto.response.TransferenciaResponse;
import com.banco.digital.domain.exception.ContaInativaException;
import com.banco.digital.domain.exception.ContaNaoEncontradaException;
import com.banco.digital.domain.exception.SaldoInsuficienteException;
import com.banco.digital.domain.exception.TransferenciaInvalidaException;
import com.banco.digital.domain.model.Cliente;
import com.banco.digital.domain.model.Conta;
import com.banco.digital.domain.model.Transferencia;
import com.banco.digital.domain.model.enums.StatusTransferencia;
import com.banco.digital.infrastructure.notification.NotificacaoService;
import com.banco.digital.infrastructure.repository.ContaRepository;
import com.banco.digital.infrastructure.repository.TransferenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransferenciaService - Testes Unitários")
class TransferenciaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private TransferenciaService transferenciaService;

    private Cliente clienteOrigem;
    private Cliente clienteDestino;
    private Conta contaOrigem;
    private Conta contaDestino;


    @BeforeEach
    void setUp() {
        clienteOrigem = Cliente.builder()
                .id(1L)
                .nome("Ana Souza")
                .cpf("111.111.111-11")
                .email("ana@email.com")
                .build();

        clienteDestino = Cliente.builder()
                .id(2L)
                .nome("Bruno Lima")
                .cpf("222.222.222-22")
                .email("bruno@email.com")
                .build();

        contaOrigem = Conta.builder()
                .id(10L)
                .numeroConta("0001-1")
                .saldo(new BigDecimal("1000.00"))
                .ativa(true)
                .cliente(clienteOrigem)
                .build();

        contaDestino = Conta.builder()
                .id(20L)
                .numeroConta("0002-1")
                .saldo(new BigDecimal("500.00"))
                .ativa(true)
                .cliente(clienteDestino)
                .build();
    }

    @Nested
    @DisplayName("Transferência bem-sucedida")
    class TransferenciaSucesso {

        @BeforeEach
        void mockRepositories() {
            // idOrigem < idDestino, então lock é adquirido em ordem: origem primeiro
            when(contaRepository.findByNumeroConta("0001-1")).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findByNumeroConta("0002-1")).thenReturn(Optional.of(contaDestino));
            when(transferenciaRepository.save(any(Transferencia.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(contaRepository.save(any(Conta.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
        }

        @Test
        @DisplayName("Deve transferir valor e atualizar saldos corretamente")
        void deveTransferirEAtualizarSaldos() {
            var request = new TransferenciaRequest("0001-1", "0002-1", new BigDecimal("200.00"), "Teste");

            TransferenciaResponse response = transferenciaService.transferir(request);

            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(StatusTransferencia.CONCLUIDA);
            assertThat(contaOrigem.getSaldo()).isEqualByComparingTo("800.00");
            assertThat(contaDestino.getSaldo()).isEqualByComparingTo("700.00");
        }

        @Test
        @DisplayName("Deve disparar notificação assíncrona após transferência")
        void deveDispararNotificacaoAposTransferencia() {
            var request = new TransferenciaRequest("0001-1", "0002-1", new BigDecimal("100.00"), null);

            transferenciaService.transferir(request);

            verify(notificacaoService, times(1)).notificarTransferencia(
                    any(), any(), any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve salvar a transferência com status CONCLUIDA")
        void deveSalvarTransferenciaComStatusConcluida() {
            var request = new TransferenciaRequest("0001-1", "0002-1", new BigDecimal("50.00"), "Pagamento");

            var captor = ArgumentCaptor.forClass(Transferencia.class);
            transferenciaService.transferir(request);

            verify(transferenciaRepository, times(2)).save(captor.capture());
            var transferenciaFinal = captor.getAllValues().get(1);
            assertThat(transferenciaFinal.getStatus()).isEqualTo(StatusTransferencia.CONCLUIDA);
            assertThat(transferenciaFinal.getCompletedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Validações de negócio")
    class ValidacoesNegocio {

        @Test
        @DisplayName("Deve lançar TransferenciaInvalidaException quando origem e destino são iguais")
        void deveLancarExcecaoQuandoContasIguais() {
            var request = new TransferenciaRequest("0001-1", "0001-1", BigDecimal.TEN, null);
            assertThatThrownBy(() -> transferenciaService.transferir(request))
                    .isInstanceOf(TransferenciaInvalidaException.class)
                    .hasMessageContaining("iguais");

            verifyNoInteractions(transferenciaRepository);
        }

        @Test
        @DisplayName("Deve lançar SaldoInsuficienteException quando saldo é insuficiente")
        void deveLancarExcecaoQuandoSaldoInsuficiente() {
            when(contaRepository.findByNumeroConta("0001-1")).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findByNumeroConta("0002-1")).thenReturn(Optional.of(contaDestino));
            when(transferenciaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var request = new TransferenciaRequest("0001-1", "0002-1", new BigDecimal("9999.00"), null);

            assertThatThrownBy(() -> transferenciaService.transferir(request))
                    .isInstanceOf(SaldoInsuficienteException.class);
        }

        @Test
        @DisplayName("Deve lançar ContaInativaException quando conta de origem está inativa")
        void deveLancarExcecaoQuandoContaOrigemInativa() {
            contaOrigem.setAtiva(false);
            when(contaRepository.findByNumeroConta("0001-1")).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findByNumeroConta("0002-1")).thenReturn(Optional.of(contaDestino));

            var request = new TransferenciaRequest("0001-1", "0002-1", new BigDecimal("100.00"), null);

            assertThatThrownBy(() -> transferenciaService.transferir(request))
                    .isInstanceOf(ContaInativaException.class);
        }

        @Test
        @DisplayName("Deve lançar ContaInativaException quando conta de destino está inativa")
        void deveLancarExcecaoQuandoContaDestinoInativa() {
            contaDestino.setAtiva(false);
            when(contaRepository.findByNumeroConta("0001-1")).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findByNumeroConta("0002-1")).thenReturn(Optional.of(contaDestino));

            var request = new TransferenciaRequest("0001-1", "0002-1", new BigDecimal("100.00"), null);

            assertThatThrownBy(() -> transferenciaService.transferir(request))
                    .isInstanceOf(ContaInativaException.class);
        }

        @Test
        @DisplayName("Deve lançar ContaNaoEncontradaException quando conta de origem não existe")
        void deveLancarExcecaoQuandoContaOrigemNaoEncontrada() {
            when(contaRepository.findByNumeroConta("0001-1")).thenReturn(Optional.empty());
            var request = new TransferenciaRequest("0001-1", "0002-1", BigDecimal.TEN, null);
            assertThatThrownBy(() -> transferenciaService.transferir(request))
                    .isInstanceOf(ContaNaoEncontradaException.class);
        }
    }
}
