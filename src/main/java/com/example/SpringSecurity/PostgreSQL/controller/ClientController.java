package com.example.SpringSecurity.PostgreSQL.controller;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateClientRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ApiResponse;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ClientResponse;
import com.example.SpringSecurity.PostgreSQL.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getAllClients() {
        List<ClientResponse> clients = clientService.getAllClients();
        ApiResponse<List<ClientResponse>> response = ApiResponse.success(clients);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> getClient(@PathVariable Long id) {
        ClientResponse client = clientService.getClientById(id);
        ApiResponse<ClientResponse> response = ApiResponse.success(client);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/newClient")
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(@Valid @RequestBody CreateClientRequest newClient) {
        ClientResponse client = clientService.createClient(newClient);
        ApiResponse<ClientResponse> response = ApiResponse.success(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
