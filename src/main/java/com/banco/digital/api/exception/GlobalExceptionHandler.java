package com.banco.digital.api.exception;

import com.banco.digital.api.dto.response.ErroResponse;
import com.banco.digital.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ErroResponse.of(400, "Erro de validação nos campos enviados.", detalhes));
    }

    @ExceptionHandler({
            ContaNaoEncontradaException.class,
            ClienteNaoEncontradoException.class,
            TransferenciaNaoEncontradaException.class
    })
    public ResponseEntity<ErroResponse> handleNotFound(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErroResponse.of(404, ex.getMessage()));
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErroResponse> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErroResponse.of(422, ex.getMessage()));
    }

    @ExceptionHandler({
            ContaInativaException.class,
            TransferenciaInvalidaException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErroResponse> handleBadRequest(RuntimeException ex) {
        return ResponseEntity
                .badRequest()
                .body(ErroResponse.of(400, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(Exception ex) {
        log.error("Erro interno não tratado: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErroResponse.of(500, "Erro interno no servidor. Tente novamente mais tarde."));
    }
}
