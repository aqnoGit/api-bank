package com.banco.digital.application.service;

import com.banco.digital.api.dto.request.CadastroClienteRequest;
import com.banco.digital.api.dto.response.ClienteResponse;
import com.banco.digital.domain.exception.ClienteNaoEncontradoException;
import com.banco.digital.domain.model.Cliente;
import com.banco.digital.domain.model.Conta;
import com.banco.digital.infrastructure.repository.ClienteRepository;
import com.banco.digital.infrastructure.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;
    private final NumerContaGenerator numerContaGenerator;

    @Transactional
    public ClienteResponse cadastrar(CadastroClienteRequest request) {
        if (clienteRepository.existsByCpf(request.cpf())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com o CPF: " + request.cpf());
        }
        if (clienteRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com o e-mail: " + request.email());
        }

        Cliente cliente = Cliente.builder()
                .nome(request.nome())
                .cpf(request.cpf())
                .email(request.email())
                .build();

        cliente = clienteRepository.save(cliente);

        Conta conta = Conta.builder()
                .numeroConta(numerContaGenerator.gerar())
                .saldo(request.saldoInicial())
                .cliente(cliente)
                .build();

        contaRepository.save(conta);
        cliente.getContas().add(conta);

        log.info("Cliente cadastrado com sucesso. ID: {}, Conta: {}", cliente.getId(), conta.getNumeroConta());
        return ClienteResponse.from(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
        return ClienteResponse.from(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteResponse::from)
                .toList();
    }
}
