package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateClientRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ClientResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.Client;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions.*;
import com.example.SpringSecurity.PostgreSQL.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserService userService;

    public ClientService(ClientRepository clientRepository, UserService userService) {
        this.clientRepository = clientRepository;
        this.userService = userService;
    }


    private ClientResponse mapToResponse(Client client) {
        return new ClientResponse(
                client.getName(),
                client.getEmail(),
                client.getPhone()
        );
    }

    @Transactional
    public ClientResponse createClient(CreateClientRequest newClient) {
        User broker = userService.findUser();
        Client client = new Client();
        client.setName(newClient.name());
        client.setEmail(newClient.email());
        client.setPhone(newClient.phone());
        client.setBroker(broker);
        try{
            Client savedClient = clientRepository.save(client);
            return mapToResponse(savedClient);
        }catch(Exception e){
            throw new ClientCreationException("Erro ao criar cliente - " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id){
        User broker = userService.findUser();
        Client client = clientRepository.findByIdAndBroker(id, broker)
                .orElseThrow(() -> new ClientNotFoundException("Cliente nao encontrado"));
        return mapToResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients(){
        try{
            User broker = userService.findUser();
            return clientRepository.findByBroker(broker)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }catch(Exception e){
            throw new ClientRetrievalException("Erro ao recuperar clientes - " + e.getMessage());
        }
    }

    @Transactional
    public ClientResponse updateClient(Long id, CreateClientRequest updatedClient){
        User broker = userService.findUser();
        Client client = clientRepository.findByIdAndBroker(id, broker)
                .orElseThrow(() -> new ClientNotFoundException("Cliente nao encontrado"));

        client.setName(updatedClient.name());
        client.setEmail(updatedClient.email());
        client.setPhone(updatedClient.phone());
        try{
            Client savedClient = clientRepository.save(client);
            return mapToResponse(savedClient);
        }catch(Exception e){
            throw new ClientUpdateException("Erro ao atualizar cliente - " + e.getMessage());
        }
    }

    @Transactional
    public void deleteClient(Long id){
        User broker = userService.findUser();
        Client client = clientRepository.findByIdAndBroker(id, broker)
                .orElseThrow(() -> new ClientNotFoundException("Cliente nao encontrado"));
        try{
            clientRepository.delete(client);
        }catch(Exception e){
            throw new ClientDeleteException("Erro ao deletar cliente - " + e.getMessage());
        }
    }

}
