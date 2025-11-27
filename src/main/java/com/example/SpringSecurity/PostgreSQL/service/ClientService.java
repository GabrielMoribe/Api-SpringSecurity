package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateClientRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ClientResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.Client;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserService userService;


    private ClientResponse mapToResponse(Client client) {
        return new ClientResponse(
                client.getName(),
                client.getEmail(),
                client.getPhone()
        );
    }

    public ClientResponse createClient(CreateClientRequest newClient) {
        User broker = userService.findUser();
        Client client = new Client();
        client.setName(newClient.name());
        client.setEmail(newClient.email());
        client.setPhone(newClient.phone());
        client.setBroker(broker);
        Client savedClient = clientRepository.save(client);
        return mapToResponse(savedClient);
    }


    public ClientResponse getClientById(Long id){
        User broker = userService.findUser();
        Client client = clientRepository.findByIdAndBroker(id, broker)
                .orElseThrow(() -> new RuntimeException("Cliente nao encontrado"));
        return mapToResponse(client);
    }

    public List<ClientResponse> getAllClients(){
        User broker = userService.findUser();
        return clientRepository.findByBroker(broker)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


}
