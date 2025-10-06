package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;
    
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String clientsPage(Model model) {
        try {
            model.addAttribute("clients", clientService.getAllClients());
        } catch (Exception e) {
            model.addAttribute("clients", java.util.Collections.emptyList());
            model.addAttribute("error", "Unable to load clients at this time.");
        }
        return "clients";
    }
    
    @PostMapping("/profile/update")
    @PreAuthorize("hasRole('CLIENT')")
    public String updateClientProfile(HttpServletRequest request,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            String userEmail = authentication.getName();
            
            ClientDTO clientDto = new ClientDTO();
            clientDto.setEmail(userEmail);
            clientDto.setName(request.getParameter("name"));
            
            String password = request.getParameter("password");
            if (password == null || password.trim().isEmpty()) {
                ClientDTO currentClient = clientService.getClientByEmail(userEmail);
                clientDto.setPassword(currentClient.getPassword());
                clientDto.setBalance(currentClient.getBalance());
            } else {
                ClientDTO currentClient = clientService.getClientByEmail(userEmail);
                clientDto.setPassword(password);
                clientDto.setBalance(currentClient.getBalance());
            }
            
            clientService.updateClientByEmail(userEmail, clientDto);
            redirectAttributes.addFlashAttribute("success", "Client profile updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/profile" + (lang != null ? "?lang=" + lang : "");
    }
    
    @PostMapping("/profile/delete")
    @PreAuthorize("hasRole('CLIENT')")
    public String deleteClientProfile(Authentication authentication, 
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        try {
            String userEmail = authentication.getName();
            
            String lang = request.getParameter("lang");
            if (lang == null) {
                lang = "en";
            }
            
            clientService.deleteClientByEmail(userEmail);
            redirectAttributes.addFlashAttribute("success", "Account deleted successfully!");
            return "redirect:/logout?lang=" + lang;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete account: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}