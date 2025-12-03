package com.example.SpringSecurity.PostgreSQL.service;

import com.example.SpringSecurity.PostgreSQL.domain.dto.request.CreateClientRequest;
import com.example.SpringSecurity.PostgreSQL.domain.dto.response.ClientResponse;
import com.example.SpringSecurity.PostgreSQL.domain.entity.Client;
import com.example.SpringSecurity.PostgreSQL.domain.entity.User;
import com.example.SpringSecurity.PostgreSQL.exceptions.clientExceptions.*;
import com.example.SpringSecurity.PostgreSQL.repository.ClientRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    public ClientResponse updateClient(Long id, CreateClientRequest updatedClient){
        User broker = userService.findUser();
        Client client = clientRepository.findByIdAndBroker(id, broker)
                .orElseThrow(() -> new ClientNotFoundException("Cliente nao encontrado"));

        try{
            client.setName(updatedClient.name());
            client.setEmail(updatedClient.email());
            client.setPhone(updatedClient.phone());

            Client savedClient = clientRepository.save(client);
            return mapToResponse(savedClient);
        }catch(ClientUpdateException e){
            throw new ClientUpdateException("Erro ao atualizar cliente - " + e.getMessage());
        }
    }

    public void deleteClient(Long id){
        User broker = userService.findUser();
        Client client = clientRepository.findByIdAndBroker(id, broker)
                .orElseThrow(() -> new ClientNotFoundException("Cliente nao encontrado"));
        try{
            clientRepository.delete(client);
        }catch(ClientDeleteException e){
            throw new ClientDeleteException("Erro ao deletar cliente - " + e.getMessage());
        }
    }

}
