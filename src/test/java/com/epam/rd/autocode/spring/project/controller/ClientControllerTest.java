package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testClientsPage() {
        when(clientService.getAllClients()).thenReturn(new ArrayList<>());

        String result = clientController.clientsPage(model);

        assertEquals("clients", result);
        verify(clientService).getAllClients();
        verify(model).addAttribute("clients", new ArrayList<>());
    }

    @Test
    void testClientsPageWithException() {
        when(clientService.getAllClients()).thenThrow(new RuntimeException("Error"));

        String result = clientController.clientsPage(model);

        assertEquals("clients", result);
        verify(model).addAttribute("clients", Collections.emptyList());
        verify(model).addAttribute("error", "Unable to load clients at this time.");
    }

    @Test
    void testUpdateClientProfile() {
        String email = "client@test.com";
        ClientDTO currentClient = new ClientDTO();
        currentClient.setPassword("oldPassword");
        currentClient.setBalance(new BigDecimal("100.0"));
        
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("name")).thenReturn("Updated Name");
        when(request.getParameter("password")).thenReturn("");
        when(request.getParameter("lang")).thenReturn("en");
        when(clientService.getClientByEmail(email)).thenReturn(currentClient);

        String result = clientController.updateClientProfile(request, authentication, redirectAttributes);

        assertEquals("redirect:/profile?lang=en", result);
        verify(clientService).updateClientByEmail(eq(email), any(ClientDTO.class));
        verify(redirectAttributes).addFlashAttribute("success", "Client profile updated successfully!");
    }

    @Test
    void testUpdateClientProfileWithNewPassword() {
        String email = "client@test.com";
        ClientDTO currentClient = new ClientDTO();
        currentClient.setBalance(new BigDecimal("100.0"));
        
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("name")).thenReturn("Updated Name");
        when(request.getParameter("password")).thenReturn("newPassword");
        when(request.getParameter("lang")).thenReturn("en");
        when(clientService.getClientByEmail(email)).thenReturn(currentClient);

        String result = clientController.updateClientProfile(request, authentication, redirectAttributes);

        assertEquals("redirect:/profile?lang=en", result);
        verify(clientService).updateClientByEmail(eq(email), any(ClientDTO.class));
        verify(redirectAttributes).addFlashAttribute("success", "Client profile updated successfully!");
    }

    @Test
    void testUpdateClientProfileWithException() {
        String email = "client@test.com";
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("name")).thenReturn("Updated Name");
        when(request.getParameter("password")).thenReturn("");
        when(request.getParameter("lang")).thenReturn("en");
        when(clientService.getClientByEmail(email)).thenThrow(new RuntimeException("Error"));

        String result = clientController.updateClientProfile(request, authentication, redirectAttributes);

        assertEquals("redirect:/profile?lang=en", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Failed to update profile"));
    }

    @Test
    void testDeleteClientProfile() {
        String email = "client@test.com";
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("lang")).thenReturn("en");

        String result = clientController.deleteClientProfile(authentication, redirectAttributes, request);

        assertEquals("redirect:/logout?lang=en", result);
        verify(clientService).deleteClientByEmail(email);
        verify(redirectAttributes).addFlashAttribute("success", "Account deleted successfully!");
    }

    @Test
    void testDeleteClientProfileWithException() {
        String email = "client@test.com";
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("lang")).thenReturn("en");
        doThrow(new RuntimeException("Error")).when(clientService).deleteClientByEmail(email);

        String result = clientController.deleteClientProfile(authentication, redirectAttributes, request);

        assertEquals("redirect:/profile", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Failed to delete account"));
    }

    @Test
    void testDeleteClientProfileWithNullLang() {
        String email = "client@test.com";
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("lang")).thenReturn(null);

        String result = clientController.deleteClientProfile(authentication, redirectAttributes, request);

        assertEquals("redirect:/logout?lang=en", result);
        verify(clientService).deleteClientByEmail(email);
        verify(redirectAttributes).addFlashAttribute("success", "Account deleted successfully!");
    }
}