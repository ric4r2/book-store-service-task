package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final BookService bookService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Add some sample data to display in the template
        model.addAttribute("totalBooks", "12,500+");
        model.addAttribute("totalCustomers", "8,200+");
        model.addAttribute("totalOrders", "35,000+");
        
        return "index";
    }
    
    @GetMapping(value = "/books", produces = "text/html")
    public String books(Model model) {
        try {
            // Fetch books from the service (same data as REST endpoint)
            model.addAttribute("books", bookService.getAllBooks());
        } catch (Exception e) {
            // In case of error, provide empty list
            model.addAttribute("books", java.util.Collections.emptyList());
            model.addAttribute("error", "Unable to load books at this time.");
        }
        return "books";
    }
    
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            boolean isEmployee = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            
            if (isEmployee) {
                EmployeeDTO employee = employeeService.getEmployeeByEmail(userEmail);
                model.addAttribute("user", employee);
                model.addAttribute("isEmployee", true);
            } else {
                ClientDTO client = clientService.getClientByEmail(userEmail);
                model.addAttribute("user", client);
                model.addAttribute("isEmployee", false);
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load profile data.");
        }
        return "profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") @Valid Object userDto, 
                               BindingResult bindingResult,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "profile";
        }
        
        try {
            String userEmail = authentication.getName();
            boolean isEmployee = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            
            if (isEmployee && userDto instanceof EmployeeDTO) {
                employeeService.updateEmployeeByEmail(userEmail, (EmployeeDTO) userDto);
                redirectAttributes.addFlashAttribute("success", "Employee profile updated successfully!");
            } else if (!isEmployee && userDto instanceof ClientDTO) {
                clientService.updateClientByEmail(userEmail, (ClientDTO) userDto);
                redirectAttributes.addFlashAttribute("success", "Client profile updated successfully!");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String userEmail = authentication.getName();
            boolean isEmployee = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            
            if (isEmployee) {
                employeeService.deleteEmployeeByEmail(userEmail);
            } else {
                clientService.deleteClientByEmail(userEmail);
            }
            
            redirectAttributes.addFlashAttribute("success", "Account deleted successfully!");
            return "redirect:/logout";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete account: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}
