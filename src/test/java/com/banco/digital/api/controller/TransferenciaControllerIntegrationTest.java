package com.banco.digital.api.controller;

import com.banco.digital.api.dto.request.TransferenciaRequest;
import com.banco.digital.domain.model.Cliente;
import com.banco.digital.domain.model.Conta;
import com.banco.digital.infrastructure.repository.ClienteRepository;
import com.banco.digital.infrastructure.repository.ContaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("TransferenciaController - Testes de Integração")
class TransferenciaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaRepository contaRepository;

    private String numeroContaOrigem;
    private String numeroContaDestino;

    @BeforeEach
    void setUp() {
        Cliente c1 = clienteRepository.save(Cliente.builder()
                .nome("João").cpf("100.200.300-01").email("joao@teste.com").build());
        Cliente c2 = clienteRepository.save(Cliente.builder()
                .nome("Maria").cpf("100.200.300-02").email("maria@teste.com").build());

        Conta origem = contaRepository.save(Conta.builder()
                .numeroConta("TEST-001").saldo(new BigDecimal("2000.00")).ativa(true).cliente(c1).build());
        Conta destino = contaRepository.save(Conta.builder()
                .numeroConta("TEST-002").saldo(new BigDecimal("500.00")).ativa(true).cliente(c2).build());

        numeroContaOrigem = origem.getNumeroConta();
        numeroContaDestino = destino.getNumeroConta();
    }

    @Test
    @DisplayName("POST /transferencias - deve retornar 201 e saldos atualizados")
    void deveRealizarTransferenciaComSucesso() throws Exception {
        var request = new TransferenciaRequest(numeroContaOrigem, numeroContaDestino, new BigDecimal("300.00"), "Pagamento");

        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.valor").value(300.00));
    }

    @Test
    @DisplayName("POST /transferencias - deve retornar 422 para saldo insuficiente")
    void deveRetornar422QuandoSaldoInsuficiente() throws Exception {
        var request = new TransferenciaRequest(numeroContaOrigem, numeroContaDestino, new BigDecimal("99999.00"), null);

        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", containsString("Saldo insuficiente")));
    }

    @Test
    @DisplayName("POST /transferencias - deve retornar 400 para contas iguais")
    void deveRetornar400QuandoContasIguais() throws Exception {
        var request = new TransferenciaRequest(numeroContaOrigem, numeroContaDestino, new BigDecimal("100.00"), null);

        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /transferencias - deve retornar 400 para valor zero")
    void deveRetornar400ParaValorZero() throws Exception {
        var request = new TransferenciaRequest(numeroContaOrigem, numeroContaDestino, BigDecimal.ZERO, null);

        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /transferencias/extrato/{contaId} - deve retornar extrato paginado")
    void deveRetornarExtratoPaginado() throws Exception {
        // Realiza uma transferência primeiro
        var request = new TransferenciaRequest(numeroContaOrigem, numeroContaDestino, new BigDecimal("100.00"), "Teste extrato");
        mockMvc.perform(post("/api/v1/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Consulta extrato
        mockMvc.perform(get("/api/v1/transferencias/extrato/{id}", numeroContaOrigem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.totalElements").value(greaterThan(0)));
    }

    @Test
    @DisplayName("GET /transferencias/{id} - deve retornar 404 para ID inexistente")
    void deveRetornar404ParaTransferenciaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/transferencias/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
