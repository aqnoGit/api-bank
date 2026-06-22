package com.banco.digital.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço de notificação assíncrono.
 * Recebe apenas tipos primitivos/value objects para evitar lazy-loading
 * fora da sessão JPA (@Async roda em thread separada, sem EntityManager ativo).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacaoService {

    private final RestTemplate restTemplate;

    @Value("${notification.webhook-url}")
    private String webhookUrl;

    @Async("notificationExecutor")
    public void notificarTransferencia(
            Long transferenciaId,
            Long contaOrigemId,
            Long contaDestinoId,
            BigDecimal valor,
            String status,
            String nomeRemetente,
            String nomeDestinatario) {

        log.info("Enviando notificação para transferência ID: {}", transferenciaId);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("transferenciaId", transferenciaId);
            payload.put("contaOrigemId", contaOrigemId);
            payload.put("contaDestinoId", contaDestinoId);
            payload.put("valor", valor);
            payload.put("status", status);
            payload.put("nomeRemetente", nomeRemetente);
            payload.put("nomeDestinatario", nomeDestinatario);

            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, payload, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Notificação enviada com sucesso para transferência ID: {}", transferenciaId);
            } else {
                log.warn("Notificação retornou status inesperado: {} para transferência ID: {}",
                        response.getStatusCode(), transferenciaId);
            }
        } catch (Exception ex) {
            log.error("Falha ao enviar notificação para transferência ID: {}. Erro: {}",
                    transferenciaId, ex.getMessage());
        }
    }
}