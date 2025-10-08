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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final BookService bookService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalBooks", "12,500+");
        model.addAttribute("totalCustomers", "8,200+");
        model.addAttribute("totalOrders", "35,000+");
        
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping(value = "/books", produces = "text/html")
    public String books(Model model,
                       @RequestParam(defaultValue = "") String search,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "15") int size,
                       @RequestParam(defaultValue = "name") String sort,
                       @RequestParam(defaultValue = "asc") String direction) {
        try {
            var booksPage = bookService.getBooks(search, page, size, sort, direction);
            
            model.addAttribute("booksPage", booksPage);
            model.addAttribute("books", booksPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", booksPage.getTotalPages());
            model.addAttribute("totalElements", booksPage.getTotalElements());
            model.addAttribute("search", search);
            model.addAttribute("sort", sort);
            model.addAttribute("direction", direction);
            model.addAttribute("size", size);
            
        } catch (Exception e) {
            model.addAttribute("books", java.util.Collections.emptyList());
            model.addAttribute("booksPage", null);
            model.addAttribute("error", "Unable to load books at this time.");
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0);
            model.addAttribute("search", search);
            model.addAttribute("sort", sort);
            model.addAttribute("direction", direction);
            model.addAttribute("size", size);
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
}
