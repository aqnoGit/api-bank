package com.banco.digital.api.controller;

import com.banco.digital.api.dto.response.ContaResponse;
import com.banco.digital.api.dto.response.ErroResponse;
import com.banco.digital.application.service.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contas")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Consulta e gestão de contas bancárias")
public class ContaController {

    private final ContaService contaService;

    @Operation(summary = "Buscar conta por número da conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/{numeroConta}")
    public ResponseEntity<ContaResponse> buscarPorNumeroConta(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.buscarPorNumeroConta(numeroConta));
    }

    @Operation(summary = "Listar contas de um cliente")
    @ApiResponse(responseCode = "200", description = "Contas encontradas")
    @GetMapping("/cliente/{documento}")
    public ResponseEntity<List<ContaResponse>> listarPorCliente(@PathVariable String documento) {
        return ResponseEntity.ok(contaService.listarPorCliente(documento));
    }

    @Operation(summary = "Desativar conta", description = "Marca a conta como inativa, impedindo novas operações")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta desativada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PatchMapping("/{numeroConta}/desativar")
    public ResponseEntity<ContaResponse> desativar(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.desativar(numeroConta));
    }
}