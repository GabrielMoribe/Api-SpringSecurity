package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateClientRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ClientResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.Client;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions.ClientCreationException;
import com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions.ClientNotFoundException;
import com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions.ClientRetrievalException;
import com.example.SpringSecurity.PostgreSQL.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        if (broker == null) throw new UsernameNotFoundException("Usuario " + newClient.name() + " - " + newClient.email() + " nao encontrado.");
        try{
            Client client = new Client();
            client.setName(newClient.name());
            client.setEmail(newClient.email());
            client.setPhone(newClient.phone());
            client.setBroker(broker);
            Client savedClient = clientRepository.save(client);
            return mapToResponse(savedClient);
        }catch(ClientCreationException e){
            throw new ClientCreationException("Erro ao criar cliente - " + e.getMessage());
        }
    }


    public ClientResponse getClientById(Long id){
        User broker = userService.findUser();
        Client client = clientRepository.findByIdAndBroker(id, broker)
                .orElseThrow(() -> new ClientNotFoundException("Cliente nao encontrado"));
        return mapToResponse(client);
    }


    public List<ClientResponse> getAllClients(){
        try{
            User broker = userService.findUser();
            return clientRepository.findByBroker(broker)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }catch(ClientRetrievalException e){
            throw new ClientRetrievalException("Erro ao recuperar clientes - " + e.getMessage());
        }
    }


}
