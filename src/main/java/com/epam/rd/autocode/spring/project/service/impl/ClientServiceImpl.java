package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        log.debug("Fetching all clients");
        return clientRepository.findAll()
                .stream()
                .map(client -> {
                    ClientDTO dto = modelMapper.map(client, ClientDTO.class);
                    dto.setPassword(null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientByEmail(String email) {
        log.debug("Fetching client by email: {}", email);
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + email));
        ClientDTO dto = modelMapper.map(client, ClientDTO.class);
        dto.setPassword(null);
        return dto;
    }

    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO clientDTO) {
        log.debug("Updating client with email: {}", email);
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + email));

        if (!email.equals(clientDTO.getEmail()) && clientRepository.existsByEmail(clientDTO.getEmail())) {
            throw new AlreadyExistException("Client already exists with email: " + clientDTO.getEmail());
        }

        client.setEmail(clientDTO.getEmail());
        client.setName(clientDTO.getName());
        client.setBalance(clientDTO.getBalance());

        if (clientDTO.getPassword() != null && !clientDTO.getPassword().isEmpty()) {
            client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        }

        client = clientRepository.save(client);
        log.info("Client updated successfully: {}", email);
        ClientDTO resultDto = modelMapper.map(client, ClientDTO.class);
        resultDto.setPassword(null);
        return resultDto;
    }

    @Override
    public void deleteClientByEmail(String email) {
        log.debug("Deleting client with email: {}", email);
        if (!clientRepository.existsByEmail(email)) {
            throw new NotFoundException("Client not found with email: " + email);
        }
        clientRepository.deleteByEmail(email);
        log.info("Client deleted successfully: {}", email);
    }

    @Override
    public ClientDTO addClient(ClientDTO clientDTO) {
        log.debug("Adding new client: {}", clientDTO.getEmail());
        if (clientRepository.existsByEmail(clientDTO.getEmail())) {
            throw new AlreadyExistException("Client already exists with email: " + clientDTO.getEmail());
        }

        Client client = modelMapper.map(clientDTO, Client.class);
        client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
        client = clientRepository.save(client);
        log.info("Client added successfully: {}", client.getEmail());
        ClientDTO resultDto = modelMapper.map(client, ClientDTO.class);
        resultDto.setPassword(null);
        return resultDto;
    }
}