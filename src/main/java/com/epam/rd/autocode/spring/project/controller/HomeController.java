package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;

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
    public String updateProfile(HttpServletRequest request,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String userEmail = authentication.getName();
            boolean isEmployee = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            
            if (isEmployee) {
                // Create EmployeeDTO from form parameters
                EmployeeDTO employeeDto = new EmployeeDTO();
                employeeDto.setEmail(userEmail);
                employeeDto.setName(request.getParameter("name"));
                employeeDto.setPhone(request.getParameter("phone"));
                
                // Handle birth date
                String birthDateStr = request.getParameter("birthDate");
                if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
                    employeeDto.setBirthDate(java.time.LocalDate.parse(birthDateStr));
                }
                
                // Handle password
                String password = request.getParameter("password");
                if (password == null || password.trim().isEmpty()) {
                    // Keep current password
                    EmployeeDTO currentEmployee = employeeService.getEmployeeByEmail(userEmail);
                    employeeDto.setPassword(currentEmployee.getPassword());
                } else {
                    employeeDto.setPassword(password);
                }
                
                employeeService.updateEmployeeByEmail(userEmail, employeeDto);
                redirectAttributes.addFlashAttribute("success", "Employee profile updated successfully!");
                
            } else {
                // Create ClientDTO from form parameters
                ClientDTO clientDto = new ClientDTO();
                clientDto.setEmail(userEmail);
                clientDto.setName(request.getParameter("name"));
                
                // Handle password
                String password = request.getParameter("password");
                if (password == null || password.trim().isEmpty()) {
                    // Keep current password and balance
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
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication authentication, 
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        try {
            String userEmail = authentication.getName();
            boolean isEmployee = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            
            // Get current language parameter
            String lang = request.getParameter("lang");
            if (lang == null) {
                lang = "en"; // default fallback
            }
            
            if (isEmployee) {
                employeeService.deleteEmployeeByEmail(userEmail);
            } else {
                clientService.deleteClientByEmail(userEmail);
            }
            
            redirectAttributes.addFlashAttribute("success", "Account deleted successfully!");
            return "redirect:/logout?lang=" + lang;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete account: " + e.getMessage());
            return "redirect:/profile";
        }
    }
    
    // Book Management Methods for Employees
    
    @PostMapping("/books/add")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String addBook(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            BookDTO bookDto = new BookDTO();
            bookDto.setName(request.getParameter("name"));
            bookDto.setGenre(request.getParameter("genre"));
            bookDto.setAgeGroup(AgeGroup.valueOf(request.getParameter("ageGroup")));
            bookDto.setPrice(new BigDecimal(request.getParameter("price")));
            bookDto.setAuthor(request.getParameter("author"));
            bookDto.setPages(Integer.parseInt(request.getParameter("pages")));
            bookDto.setCharacteristics(request.getParameter("characteristics"));
            bookDto.setDescription(request.getParameter("description"));
            bookDto.setLanguage(Language.valueOf(request.getParameter("language")));
            
            // Parse publication date
            String publicationDateStr = request.getParameter("publicationDate");
            if (publicationDateStr != null && !publicationDateStr.trim().isEmpty()) {
                bookDto.setPublicationDate(LocalDate.parse(publicationDateStr));
            }
            
            bookService.addBook(bookDto);
            redirectAttributes.addFlashAttribute("success", "Book added successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add book: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/books" + (lang != null ? "?lang=" + lang : "");
    }
    
    @PostMapping("/books/edit/{name}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String editBook(@PathVariable String name, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            BookDTO bookDto = new BookDTO();
            bookDto.setName(request.getParameter("name"));
            bookDto.setGenre(request.getParameter("genre"));
            bookDto.setAgeGroup(AgeGroup.valueOf(request.getParameter("ageGroup")));
            bookDto.setPrice(new BigDecimal(request.getParameter("price")));
            bookDto.setAuthor(request.getParameter("author"));
            bookDto.setPages(Integer.parseInt(request.getParameter("pages")));
            bookDto.setCharacteristics(request.getParameter("characteristics"));
            bookDto.setDescription(request.getParameter("description"));
            bookDto.setLanguage(Language.valueOf(request.getParameter("language")));
            
            // Parse publication date
            String publicationDateStr = request.getParameter("publicationDate");
            if (publicationDateStr != null && !publicationDateStr.trim().isEmpty()) {
                bookDto.setPublicationDate(LocalDate.parse(publicationDateStr));
            }
            
            bookService.updateBookByName(name, bookDto);
            redirectAttributes.addFlashAttribute("success", "Book updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update book: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/books" + (lang != null ? "?lang=" + lang : "");
    }
    
    @PostMapping("/books/delete/{name}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String deleteBook(@PathVariable String name, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBookByName(name);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete book: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/books" + (lang != null ? "?lang=" + lang : "");
    }
}
