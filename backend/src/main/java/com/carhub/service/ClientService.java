package com.carhub.service;

import com.carhub.dto.ClientDTO;
import com.carhub.entity.Client;
import com.carhub.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Page<Client> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));
    }

    public Client createClient(ClientDTO clientDTO) {
        Client client = new Client();
        mapDtoToEntity(clientDTO, client);
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, ClientDTO clientDTO) {
        Client client = getClientById(id);
        mapDtoToEntity(clientDTO, client);
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        Client client = getClientById(id);
        clientRepository.delete(client);
    }

    public Page<Client> searchClients(String search, Pageable pageable) {
        return clientRepository.searchClients(search, pageable);
    }

    private void mapDtoToEntity(ClientDTO dto, Client entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setZipCode(dto.getZipCode());
    }
}
