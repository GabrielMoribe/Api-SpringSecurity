package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.service.PaymentService;
import com.example.SpringSecurity.PostgreSQL.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final UserService userService;
    private final PaymentService paymentService;

    public PaymentController(UserService userService , PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> checkout(){
        User user = userService.findUser();
        String link = paymentService.createPaymentLink(user, new BigDecimal("90.00") , "Plano Basico");
        ApiResponse<String> response = ApiResponse.success(link);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
