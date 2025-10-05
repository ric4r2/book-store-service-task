package com.epam.rd.autocode.spring.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(Model model) {
        // Add some sample data to display in the template
        model.addAttribute("totalBooks", "12,500+");
        model.addAttribute("totalCustomers", "8,200+");
        model.addAttribute("totalOrders", "35,000+");
        
        return "index";
    }
    
    @GetMapping("/books-page")
    public String books() {
        return "books";
    }
}
