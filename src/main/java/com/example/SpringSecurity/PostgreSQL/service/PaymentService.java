package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.domain.enums.SubscriptionStatus;
import com.example.SpringSecurity.PostgreSQL.repository.UserRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    private final UserRepository userRepository;

    public PaymentService(@Value("${mp.access.token}") String accessToken   , UserRepository userRepository) {
        this.userRepository = userRepository;
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public String createPaymentLink(User user, BigDecimal price , String title){
        try{
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder() // Cria o item da compra
                    .id("Plano Basico")
                    .title(title)
                    .description("Assinatura mensao do plano basico")
                    .categoryId("services")
                    .quantity(1)
                    .currencyId("BRL")
                    .unitPrice(price)
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://localhost:8080/payments/success")
                    .failure("https://localhost:8080/payments/failure")
                    .pending("https://localhost:8080/payments/pending")
                    .build();

            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder() // Dados do pagador
                    .email("cliente_teste_" + user.getId() + "@testuser.com")
                    .name(user.getName() != null ? user.getName() : "Cliente Teste")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .payer(payerRequest)
                    .autoReturn("approved")
                    .binaryMode(true)
                    .externalReference(user.getId().toString())
                    .notificationUrl("https://unblacked-brittaney-unrecondite.ngrok-free.dev/webhooks/mercadopago")
                    .build();

            PreferenceClient preferenceClient = new PreferenceClient();
            Preference preference = preferenceClient.create(preferenceRequest);
            return preference.getSandboxInitPoint();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar pagamento: " + e.getMessage());
        }
    }




    @Transactional
    public void processPaymentNotification(Long paymentId){
        try{
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            if("approved".equals(payment.getStatus())){
                Long userId = Long.parseLong(payment.getExternalReference());
                User user = userRepository.findById(userId)
                        .orElseThrow(()-> new RuntimeException("Usuario nao encontrado para o pagamento"));
                user.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
                user.setMpPaymentId(payment.getId().toString());
                userRepository.save(user);
            }
        }catch(Exception e){
            throw new RuntimeException("Erro ao processar notificacao de pagamento: " + e.getMessage());
        }
    }


}
