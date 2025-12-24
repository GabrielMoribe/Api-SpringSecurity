package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateSubscriptionRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.SubscriptionResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.SubscriptionStatus;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;

    @Value("${mercadopago.back-url}")
    private String baseUrl;

    private static final String MP_API_URL = "https://api.mercadopago.com/preapproval";

    // TODO: CRIAR TABELA NO BANCO
    private static final Map<String, PlanConfig> PLANS = Map.of(
            "basic", new PlanConfig("Plano Basic", new BigDecimal("29.90")),
            "pro", new PlanConfig("Plano Pro", new BigDecimal("49.90")),
            "enterprise", new PlanConfig("Plano Enterprise", new BigDecimal("99.90"))
    );


    public SubscriptionService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // Criar assinatura
    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        User user = userService.findUser();

        // Verifica se já possui assinatura ativa
        if (user.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
            return new SubscriptionResponse(
                    user.getSubscriptionStatus(),
                    null,
                    "Você já possui uma assinatura ativa"
            );
        }

        // Busca configuração do plano
        PlanConfig plan = PLANS.get(request.planId().toLowerCase());
        if (plan == null) {
            throw new IllegalArgumentException("Plano não encontrado: " + request.planId());
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            // URL de callback - Se for localhost usa o url definido nas properties
            String backUrl = request.backUrl();
            if (backUrl == null || backUrl.contains("localhost") || backUrl.contains("127.0.0.1")) {
                backUrl = baseUrl + "/subscriptions/callback";
            }

            // Monta corpo da requisição
            Map<String, Object> autoRecurring = new HashMap<>();
            autoRecurring.put("frequency", 1);
            autoRecurring.put("frequency_type", "months");
            autoRecurring.put("transaction_amount", plan.amount().doubleValue());
            autoRecurring.put("currency_id", "BRL");

//            Map<String, Object> body = new HashMap<>();
//            body.put("reason", plan.name());
//            String payerEmail = (request.testPayerEmail() != null && !request.testPayerEmail().isBlank())
//                    ? request.testPayerEmail()
//                    : user.getEmail();
//            body.put("payer_email", payerEmail);
//            body.put("auto_recurring", autoRecurring);
//            body.put("back_url", backUrl);
//            body.put("external_reference", user.getId().toString());
            Map<String, Object> body = new HashMap<>();
            body.put("reason", plan.name());

            // Em ambiente de teste, usa o email de teste se fornecido
            String payerEmail = (request.testPayerEmail() != null && !request.testPayerEmail().isBlank())
                    ? request.testPayerEmail()
                    : user.getEmail();
            body.put("payer_email", payerEmail);

            body.put("auto_recurring", autoRecurring);
            body.put("back_url", backUrl);
            body.put("external_reference", user.getId().toString());

            // DEBUG COMPLETO
            log.info("=== REQUEST COMPLETO PARA MERCADO PAGO ===");
            log.info("URL: https://api.mercadopago.com/preapproval");
            log.info("Access Token (primeiros 20 chars): {}", accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
            log.info("Body completo: {}", body);
            log.info("auto_recurring: {}", autoRecurring);
            log.info("==========================================");


            // Adiciona notification_url se estiver configurado
            if (notificationUrl != null && !notificationUrl.isEmpty()) {
                body.put("notification_url", notificationUrl);
            }

//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//            ResponseEntity<String> response = restTemplate.exchange(
//                    MP_API_URL,
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//
//            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.mercadopago.com/preapproval",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            // DEBUG - Log da resposta completa
            log.info("=== RESPOSTA DO MERCADO PAGO ===");
            log.info("Status: {}", response.getStatusCode());
            log.info("Body: {}", response.getBody());
            log.info("================================");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.getBody());

//            String preapprovalId = jsonResponse.get("id").asText();
//            String initPoint = jsonResponse.get("init_point").asText();
//
//            // Atualiza usuário
//            user.setMpPaymentId(preapprovalId);
//            //user.setSubscriptionStatus(SubscriptionStatus.PENDING);
//            user.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
//            userRepository.save(user);
//            log.info("Assinatura criada para usuário {}: {}", user.getEmail(), preapprovalId);
//            return new SubscriptionResponse(
//                    SubscriptionStatus.PENDING,
//                    initPoint,
//                    "Assinatura criada. Finalize o pagamento no link fornecido.");
            String preapprovalId = jsonResponse.get("id").asText();
            String initPoint = jsonResponse.get("init_point").asText();

            // Verifica se existe sandbox_init_point
            String sandboxInitPoint = jsonResponse.has("sandbox_init_point")
                    ? jsonResponse.get("sandbox_init_point").asText()
                    : null;

            log.info("init_point: {}", initPoint);
            log.info("sandbox_init_point: {}", sandboxInitPoint);

            // Em ambiente de teste, preferir sandbox_init_point
            String finalInitPoint = sandboxInitPoint != null ? sandboxInitPoint : initPoint;

            // Atualiza usuário
            user.setMpPaymentId(preapprovalId);
            user.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
            userRepository.save(user);
            log.info("Assinatura criada para usuário {}: {}", user.getEmail(), preapprovalId);
            return new SubscriptionResponse(
                    SubscriptionStatus.PENDING,
                    finalInitPoint,  // Usa o sandbox se disponível
                    "Assinatura criada. Finalize o pagamento no link fornecido.");

        } catch (Exception e) {
            log.error("Erro ao criar assinatura no Mercado Pago", e);
            throw new RuntimeException("Erro ao criar assinatura: " + e.getMessage());
        }
    }

    // Obter status da assinatura
    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscriptionStatus() {
        User user = userService.findUser();

        String message = switch (user.getSubscriptionStatus()) {
            case INACTIVE -> "Você ainda não possui uma assinatura";
            case PENDING -> "Aguardando confirmação do pagamento";
            case ACTIVE -> "Assinatura ativa";
            case SUSPENDED -> "Assinatura suspensa";
            case CANCELLED -> "Assinatura cancelada";
        };
        return new SubscriptionResponse(user.getSubscriptionStatus(), null, message);
    }

    // Cancelar assinatura
    @Transactional
    public SubscriptionResponse cancelSubscription() {
        User user = userService.findUser();

        if (user.getMpPaymentId() == null) {
            throw new IllegalStateException("Nenhuma assinatura encontrada");
        }
        if (user.getSubscriptionStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Não há assinatura ativa para cancelar");
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            Map<String, Object> body = new HashMap<>();
            body.put("status", "cancelled");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            restTemplate.exchange(
                    MP_API_URL + "/" + user.getMpPaymentId(),
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            user.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
            userRepository.save(user);
            log.info("Assinatura cancelada para usuário {}", user.getEmail());
            return new SubscriptionResponse(
                    SubscriptionStatus.CANCELLED,
                    null,
                    "Assinatura cancelada com sucesso"
            );

        } catch (Exception e) {
            log.error("Erro ao cancelar assinatura", e);
            throw new RuntimeException("Erro ao cancelar assinatura: " + e.getMessage());
        }
    }

    // Processar webhook do Mercado Pago
    @Transactional
    public void processWebhook(String type, String dataId, Map<String, Object> body) {
        String resolvedType = type;
        String resolvedDataId = dataId;
        log.info("Webhook recebido - type: {}, dataId: {}", type, dataId);

        if (resolvedType == null && body != null) {
            resolvedType = (String) body.get("type");
            if (body.get("data") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                resolvedDataId = data.get("id") != null ? data.get("id").toString() : null;
            }
        }

        log.info("Webhook recebido - type: {}, dataId: {}", resolvedType, resolvedDataId);

        if (resolvedType == null || resolvedDataId == null) {
            log.warn("Webhook ignorado - dados incompletos");
            return;
        }
        if (!"preapproval".equals(resolvedType)) {
            log.info("Webhook ignorado - tipo não é preapproval: {}", resolvedType);
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    MP_API_URL + "/" + dataId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            String status = jsonResponse.get("status").asText();

            User user = userRepository.findByMpPaymentId(dataId)
                    .orElseThrow(() -> {
                        log.warn("Usuário não encontrado para preapproval: {}", dataId);
                        return new IllegalArgumentException("Usuário não encontrado");
                    });

            SubscriptionStatus newStatus = mapMercadoPagoStatus(status);
            user.setSubscriptionStatus(newStatus);
            userRepository.save(user);

            log.info("Webhook processado - Usuário {}: {} -> {}",
                    user.getEmail(), status, newStatus);

        } catch (Exception e) {
            log.error("Erro ao processar webhook", e);
            throw new RuntimeException("Erro ao processar webhook: " + e.getMessage());
        }
    }

    //Metodo auxiliar para mapear status do Mercado Pago para SubscriptionStatus
    private SubscriptionStatus mapMercadoPagoStatus(String mpStatus) {
        if (mpStatus == null) {
            return SubscriptionStatus.INACTIVE;
        }
        return switch (mpStatus.toLowerCase()) {
            case "authorized", "active" -> SubscriptionStatus.ACTIVE;
            case "paused" -> SubscriptionStatus.SUSPENDED;
            case "cancelled" -> SubscriptionStatus.CANCELLED;
            case "pending" -> SubscriptionStatus.PENDING;
            default -> SubscriptionStatus.INACTIVE;
        };
    }

    // Processar callback após tentativa de pagamento
    public Map<String, String> processCallback(String preapprovalId, String status) {
        log.info("Callback recebido - preapproval_id: {}, status: {}", preapprovalId, status);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Assinatura processada com sucesso!");
        response.put("preapproval_id", preapprovalId);

        return response;
    }

    // TODO: CRIAR CLASSE QUANDO CRIAR TABELA NO BANCO
    private record PlanConfig(String name, BigDecimal amount) {}
}