package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final BookService bookService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Add some sample data to display in the template
        model.addAttribute("totalBooks", "12,500+");
        model.addAttribute("totalCustomers", "8,200+");
        model.addAttribute("totalOrders", "35,000+");
        
        return "index";
    }
    
    @GetMapping("/books-page")
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
}
