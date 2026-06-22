package com.banco.digital.api.controller;

import com.banco.digital.api.dto.request.TransferenciaRequest;
import com.banco.digital.api.dto.response.ErroResponse;
import com.banco.digital.api.dto.response.TransferenciaResponse;
import com.banco.digital.application.service.TransferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transferencias")
@RequiredArgsConstructor
@Tag(name = "Transferências", description = "Operações de transferência de fundos entre contas")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    @Operation(
            summary = "Realizar transferência",
            description = """
                    Transfere um valor entre duas contas bancárias.
                    
                    - Garante atomicidade via transação JPA.
                    - Controle de concorrência via pessimistic lock nas contas envolvidas.
                    - Notificação assíncrona enviada ao concluir com sucesso.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, contas iguais, conta inativa ou valor inválido",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "422", description = "Saldo insuficiente",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TransferenciaResponse> transferir(@Valid @RequestBody TransferenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferenciaService.transferir(request));
    }

    @Operation(summary = "Buscar transferências por número da conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferências encontradas"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/conta/{numeroConta}")
    public ResponseEntity<Page<TransferenciaResponse>> buscarPorNumeroConta(
            @PathVariable String numeroConta,
            @Parameter(description = "Número da página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(transferenciaService.buscarPorNumeroConta(numeroConta, pageable));
    }

    @Operation(
            summary = "Extrato da conta",
            description = "Lista todas as movimentações (enviadas e recebidas) de uma conta, com paginação"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Extrato retornado"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/extrato/{numeroConta}")
    public ResponseEntity<Page<TransferenciaResponse>> extrato(
            @PathVariable String numeroConta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(transferenciaService.listarMovimentacoes(numeroConta, pageable));
    }
}