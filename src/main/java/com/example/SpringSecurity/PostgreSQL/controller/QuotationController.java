package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.QuotationRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.QuotationResponse;
import com.example.SpringSecurity.PostgreSQL.service.QuotationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/quotations")
public class QuotationController {

    private final QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<QuotationResponse>>> getAllQuotations(){
        List<QuotationResponse> quotations = quotationService.getAllQuotations();
        ApiResponse<List<QuotationResponse>> response = ApiResponse.success(quotations);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuotationResponse>> getQuotationById(@PathVariable Long id){
        QuotationResponse quotation = quotationService.getQuotationById(id);
        ApiResponse<QuotationResponse> response = ApiResponse.success(quotation);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/newQuotation")
    public ResponseEntity<ApiResponse<QuotationResponse>> createQuotation(@Valid @RequestBody QuotationRequest quotationRequest){
        QuotationResponse quotation = quotationService.createQuotation(quotationRequest);
        ApiResponse<QuotationResponse> response = ApiResponse.success(quotation);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<QuotationResponse>> updateQuotation(@PathVariable Long id, @Valid @RequestBody QuotationRequest quotationRequest) {
        QuotationResponse quotation = quotationService.updateQuotation(id, quotationRequest);
        ApiResponse<QuotationResponse> response = ApiResponse.success(quotation);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

@DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuotation(@PathVariable Long id) {
        quotationService.deleteQuotation(id);
        ApiResponse<Void> response = ApiResponse.success(null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
