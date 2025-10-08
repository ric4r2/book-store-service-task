package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String orders(Model model, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            boolean isEmployee = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            
            if (isEmployee) {
                model.addAttribute("orders", orderService.getAllOrders());
                model.addAttribute("isEmployee", true);
            } else {
                model.addAttribute("orders", orderService.getOrdersByClient(userEmail));
                model.addAttribute("isEmployee", false);
            }
            
        } catch (Exception e) {
            model.addAttribute("orders", java.util.Collections.emptyList());
            model.addAttribute("error", "Unable to load orders at this time.");
            model.addAttribute("isEmployee", authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
        }
        return "orders";
    }
    
    @PostMapping("/approve")
    public String approveOrder(@RequestParam Long orderId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            String employeeEmail = authentication.getName();
            orderService.approveOrder(orderId, employeeEmail);
            redirectAttributes.addFlashAttribute("success", "Order approved successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve order: " + e.getMessage());
        }
        
        return "redirect:/orders";
    }
}
