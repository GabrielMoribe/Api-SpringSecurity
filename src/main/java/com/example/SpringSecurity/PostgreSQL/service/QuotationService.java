package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.QuotationRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.QuotationResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.Client;
import com.example.SpringSecurity.PostgreSQL.domain.entity.HealthPlan;
import com.example.SpringSecurity.PostgreSQL.domain.entity.Quotation;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions.ClientNotFoundException;
import com.example.SpringSecurity.PostgreSQL.repository.ClientRepository;
import com.example.SpringSecurity.PostgreSQL.repository.HealthPlanRepository;
import com.example.SpringSecurity.PostgreSQL.repository.QuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class QuotationService {

    @Autowired
    private UserService userService;
    @Autowired
    private QuotationRepository quotationRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private HealthPlanRepository healthPlanRepository;

    private QuotationResponse mapToResponse(Quotation quotation) {
        return new QuotationResponse(
                quotation.getClient().getName(),
                quotation.getHealthPlan().getName(),
                quotation.getHealthPlan().getOperator(),
                quotation.getFinalPrice(),
                quotation.getCreatedAt()
        );
    }


    public QuotationResponse createQuotation(QuotationRequest request) {
        User broker = userService.findUser();

        Client client = clientRepository.findByIdAndBroker(request.clientId(), broker)
                .orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));
        HealthPlan plan = healthPlanRepository.findById(request.healthPlanId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado")); // Criar excecao

        BigDecimal totalFinalPrice = calculateTotalFinalPrice(plan, request.beneficiariesByAge());
        Quotation quotation = new Quotation();
        quotation.setClient(client);
        quotation.setHealthPlan(plan);
        quotation.setFinalPrice(totalFinalPrice);
        quotation.setBeneficiariesByAge(request.beneficiariesByAge());
        Quotation savedQuotation = quotationRepository.save(quotation);
        return mapToResponse(savedQuotation);
    }



    public List<QuotationResponse> getAllQuotations() {
        User broker = userService.findUser();
        return quotationRepository.findByClient_Broker(broker)
                .stream()
                .map(quotation -> mapToResponse(quotation))
                .toList();
    }
    public QuotationResponse getQuotationById(Long id) {
        User broker = userService.findUser();
        Quotation quotation = quotationRepository.findByIdAndClient_Broker(id, broker)
                .orElseThrow(() -> new RuntimeException("Cotação não encontrada"));  // Criar excecao
        return mapToResponse(quotation);
    }


    public QuotationResponse updateQuotation(Long id, QuotationRequest request) {
        User broker = userService.findUser();

        Quotation existingQuotation = quotationRepository.findByIdAndClient_Broker(id, broker)
                .orElseThrow(() -> new RuntimeException("Cotação não encontrada"));  // Criar excecao
        Client client = clientRepository.findByIdAndBroker(request.clientId(), broker)
                .orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado"));  // Criar excecao
        HealthPlan plan = healthPlanRepository.findById(request.healthPlanId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));  // Criar excecao

        BigDecimal totalFinalPrice = calculateTotalFinalPrice(plan, request.beneficiariesByAge());
        existingQuotation.setClient(client);
        existingQuotation.setHealthPlan(plan);
        existingQuotation.setFinalPrice(totalFinalPrice);
        existingQuotation.setBeneficiariesByAge(request.beneficiariesByAge());
        Quotation updatedQuotation = quotationRepository.save(existingQuotation);
        return mapToResponse(updatedQuotation);
    }


    public void deleteQuotation(Long id) {
        User broker = userService.findUser();

        Quotation quotation = quotationRepository.findByIdAndClient_Broker(id, broker)
                .orElseThrow(() -> new RuntimeException("Cotação não encontrada."));  // Criar excecao
        try {
            quotationRepository.delete(quotation);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar cotação: " + e.getMessage()); // Criar excecao
        }
    }





    private BigDecimal calculateTotalFinalPrice(HealthPlan plan, Map<String, Integer> beneficiariesAges) {
        BigDecimal totalFinalPrice = BigDecimal.ZERO;
        Map<String, Double> planFactors = plan.getAgeFactor();

        for (Map.Entry<String, Integer> entry : beneficiariesAges.entrySet()) {
            String ageRange = entry.getKey();
            Integer quantity = entry.getValue();

            if (planFactors.containsKey(ageRange)) {
                Double factor = planFactors.get(ageRange);
                BigDecimal priceForThisRange = plan.getBasePrice()
                        .multiply(BigDecimal.valueOf(factor))
                        .multiply(BigDecimal.valueOf(quantity));
                totalFinalPrice = totalFinalPrice.add(priceForThisRange);
            } else {
                throw new RuntimeException("Faixa etária inválida para este plano: " + ageRange); // Criar excecao
            }
        }
        return totalFinalPrice;
    }

}