package com.banco.digital.application.service;

import com.banco.digital.api.dto.request.CadastroClienteRequest;
import com.banco.digital.api.dto.response.ClienteResponse;
import com.banco.digital.domain.exception.ClienteNaoEncontradoException;
import com.banco.digital.domain.model.Cliente;
import com.banco.digital.domain.model.Conta;
import com.banco.digital.infrastructure.repository.ClienteRepository;
import com.banco.digital.infrastructure.repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Testes Unitários")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private NumerContaGenerator numerContaGenerator;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        var request = new CadastroClienteRequest("Ana Souza", "111.111.111-11", "ana@email.com", new BigDecimal("500.00"));

        when(clienteRepository.existsByCpf(any())).thenReturn(false);
        when(clienteRepository.existsByEmail(any())).thenReturn(false);
        when(numerContaGenerator.gerar()).thenReturn("0001-1");

        Cliente clienteSalvo = Cliente.builder()
                .id(1L)
                .nome("Ana Souza")
                .cpf("111.111.111-11")
                .email("ana@email.com")
                .build();

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(contaRepository.save(any(Conta.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteResponse response = clienteService.cadastrar(request);

        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo("Ana Souza");
        assertThat(response.cpf()).isEqualTo("111.111.111-11");

        verify(clienteRepository).save(any(Cliente.class));
        verify(contaRepository).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando CPF já cadastrado")
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        var request = new CadastroClienteRequest("Ana", "111.111.111-11", "ana@email.com", BigDecimal.TEN);

        when(clienteRepository.existsByCpf("111.111.111-11")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.cadastrar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando e-mail já cadastrado")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        var request = new CadastroClienteRequest("Ana", "111.111.111-11", "ana@email.com", BigDecimal.TEN);

        when(clienteRepository.existsByCpf(any())).thenReturn(false);
        when(clienteRepository.existsByEmail("ana@email.com")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.cadastrar(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("e-mail");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ClienteNaoEncontradoException quando ID não existe")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        Long id = 1L;
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(id))
                .isInstanceOf(ClienteNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarTodosOsClientes() {
        Cliente c1 = Cliente.builder().id(1L).nome("Ana").cpf("111").email("ana@a.com").build();
        Cliente c2 = Cliente.builder().id(2L).nome("Bruno").cpf("222").email("bruno@a.com").build();

        when(clienteRepository.findAll()).thenReturn(List.of(c1, c2));

        List<ClienteResponse> resultado = clienteService.listarTodos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(ClienteResponse::nome).containsExactly("Ana", "Bruno");
    }
}