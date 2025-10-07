package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setEmail("test@email.com");
        client.setName("John Doe");
        client.setPassword("encodedPassword");
        client.setBalance(BigDecimal.valueOf(100.00));

        clientDTO = new ClientDTO();
        clientDTO.setEmail("test@email.com");
        clientDTO.setName("John Doe");
        clientDTO.setPassword("plainPassword");
        clientDTO.setBalance(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Should return all clients successfully")
    void testGetAllClients() {
        List<Client> clients = Arrays.asList(client);
        List<ClientDTO> expectedDTOs = Arrays.asList(clientDTO);

        when(clientRepository.findAll()).thenReturn(clients);
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        List<ClientDTO> result = clientService.getAllClients();

        assertNotNull(result);
        assertEquals(expectedDTOs.size(), result.size());
        assertEquals(expectedDTOs.get(0).getEmail(), result.get(0).getEmail());
        verify(clientRepository).findAll();
        verify(modelMapper).map(client, ClientDTO.class);
    }

    @Test
    @DisplayName("Should return empty list when no clients found")
    void testGetAllClientsEmpty() {
        when(clientRepository.findAll()).thenReturn(Arrays.asList());

        List<ClientDTO> result = clientService.getAllClients();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(clientRepository).findAll();
    }

    @Test
    @DisplayName("Should return client by email successfully")
    void testGetClientByEmailSuccess() {
        when(clientRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));
        when(modelMapper.map(client, ClientDTO.class)).thenReturn(clientDTO);

        ClientDTO result = clientService.getClientByEmail(client.getEmail());

        assertNotNull(result);
        assertEquals(clientDTO.getEmail(), result.getEmail());
        assertEquals(clientDTO.getName(), result.getName());
        verify(clientRepository).findByEmail(client.getEmail());
        verify(modelMapper).map(client, ClientDTO.class);
    }

    @Test
    @DisplayName("Should throw NotFoundException when client not found by email")
    void testGetClientByEmailNotFound() {
        when(clientRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            clientService.getClientByEmail("nonexistent@email.com");
        });

        verify(clientRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    @DisplayName("Should update client by email successfully")
    void testUpdateClientByEmailSuccess() {
        ClientDTO updateDTO = new ClientDTO();
        updateDTO.setEmail("test@email.com");
        updateDTO.setName("Jane Smith");
        updateDTO.setPassword("newPassword");
        updateDTO.setBalance(BigDecimal.valueOf(200.00));

        Client updatedClient = new Client();
        updatedClient.setId(1L);
        updatedClient.setEmail("test@email.com");
        updatedClient.setName("Jane Smith");
        updatedClient.setPassword("encodedNewPassword");
        updatedClient.setBalance(BigDecimal.valueOf(200.00));

        ClientDTO resultDTO = new ClientDTO();
        resultDTO.setEmail("test@email.com");
        resultDTO.setName("Jane Smith");
        resultDTO.setBalance(BigDecimal.valueOf(200.00));

        when(clientRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(client));
        when(passwordEncoder.encode(updateDTO.getPassword())).thenReturn("encodedNewPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);
        when(modelMapper.map(updatedClient, ClientDTO.class)).thenReturn(resultDTO);

        ClientDTO result = clientService.updateClientByEmail(updateDTO.getEmail(), updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO.getEmail(), result.getEmail());
        assertEquals(updateDTO.getName(), result.getName());
        assertNull(result.getPassword());
        verify(clientRepository).findByEmail(updateDTO.getEmail());
        verify(passwordEncoder).encode(updateDTO.getPassword());
        verify(clientRepository).save(any(Client.class));
        verify(modelMapper).map(updatedClient, ClientDTO.class);
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent client")
    void testUpdateClientByEmailNotFound() {
        when(clientRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            clientService.updateClientByEmail("nonexistent@email.com", clientDTO);
        });

        verify(clientRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    @DisplayName("Should delete client by email successfully")
    void testDeleteClientByEmailSuccess() {
        when(clientRepository.existsByEmail(client.getEmail())).thenReturn(true);

        clientService.deleteClientByEmail(client.getEmail());

        verify(clientRepository).existsByEmail(client.getEmail());
        verify(clientRepository).deleteByEmail(client.getEmail());
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existent client")
    void testDeleteClientByEmailNotFound() {
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            clientService.deleteClientByEmail("nonexistent@email.com");
        });

        verify(clientRepository).existsByEmail("nonexistent@email.com");
        verify(clientRepository, never()).deleteByEmail(anyString());
    }

    @Test
    @DisplayName("Should add new client successfully")
    void testAddClientSuccess() {
        Client savedClient = new Client();
        savedClient.setId(2L);
        savedClient.setEmail("new@email.com");
        savedClient.setName("New Client");
        savedClient.setPassword("encodedPassword");
        savedClient.setBalance(BigDecimal.ZERO);

        ClientDTO newClientDTO = new ClientDTO();
        newClientDTO.setEmail("new@email.com");
        newClientDTO.setName("New Client");
        newClientDTO.setPassword("plainPassword");
        newClientDTO.setBalance(BigDecimal.ZERO);

        ClientDTO resultDTO = new ClientDTO();
        resultDTO.setEmail("new@email.com");
        resultDTO.setName("New Client");
        resultDTO.setBalance(BigDecimal.ZERO);

        when(clientRepository.existsByEmail(newClientDTO.getEmail())).thenReturn(false);
        when(modelMapper.map(newClientDTO, Client.class)).thenReturn(savedClient);
        when(passwordEncoder.encode(newClientDTO.getPassword())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        when(modelMapper.map(savedClient, ClientDTO.class)).thenReturn(resultDTO);

        ClientDTO result = clientService.addClient(newClientDTO);

        assertNotNull(result);
        assertEquals(newClientDTO.getEmail(), result.getEmail());
        assertEquals(newClientDTO.getName(), result.getName());
        assertNull(result.getPassword());
        verify(clientRepository).existsByEmail(newClientDTO.getEmail());
        verify(modelMapper).map(newClientDTO, Client.class);
        verify(passwordEncoder).encode(newClientDTO.getPassword());
        verify(clientRepository).save(any(Client.class));
        verify(modelMapper).map(savedClient, ClientDTO.class);
    }

    @Test
    @DisplayName("Should throw AlreadyExistException when adding existing client")
    void testAddClientAlreadyExists() {
        when(clientRepository.existsByEmail(clientDTO.getEmail())).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> {
            clientService.addClient(clientDTO);
        });

        verify(clientRepository).existsByEmail(clientDTO.getEmail());
        verify(clientRepository, never()).save(any(Client.class));
    }
}