package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@PreAuthorize("hasRole('CLIENT')")
public class CartController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private OrderService orderService;

    // Cart item structure
    public static class CartItem {
        private BookDTO book;
        private Integer quantity;
        
        public CartItem(BookDTO book, Integer quantity) {
            this.book = book;
            this.quantity = quantity;
        }
        
        // Getters and setters
        public BookDTO getBook() { return book; }
        public void setBook(BookDTO book) { this.book = book; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getSubtotal() {
            return book.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam String bookName,
                           @RequestParam Integer quantity,
                           HttpSession session,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {
        try {
            // Get cart from session
            @SuppressWarnings("unchecked")
            Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new HashMap<>();
            }
            
            // Get book details
            BookDTO book = bookService.getBookByName(bookName);
            
            // Add or update cart item
            if (cart.containsKey(bookName)) {
                CartItem existingItem = cart.get(bookName);
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                cart.put(bookName, new CartItem(book, quantity));
            }
            
            // Save cart to session
            session.setAttribute("cart", cart);
            
            redirectAttributes.addFlashAttribute("success", 
                quantity + " x " + book.getName() + " added to cart!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add book to cart: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/books" + (lang != null ? "?lang=" + lang : "");
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
        
        if (cart == null) {
            cart = new HashMap<>();
        }
        
        List<CartItem> cartItems = new ArrayList<>(cart.values());
        BigDecimal total = cartItems.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", total);
        model.addAttribute("cartSize", cartItems.size());
        
        return "cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam String bookName,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
            
            if (cart != null && cart.containsKey(bookName)) {
                cart.remove(bookName);
                session.setAttribute("cart", cart);
                redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove item from cart");
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/cart" + (lang != null ? "?lang=" + lang : "");
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam String bookName,
                                @RequestParam Integer quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
            
            if (cart != null && cart.containsKey(bookName)) {
                if (quantity > 0) {
                    cart.get(bookName).setQuantity(quantity);
                } else {
                    cart.remove(bookName);
                }
                session.setAttribute("cart", cart);
                redirectAttributes.addFlashAttribute("success", "Cart updated!");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update cart");
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/cart" + (lang != null ? "?lang=" + lang : "");
    }

    @PostMapping("/order")
    public String createOrder(HttpSession session,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, CartItem> cart = (Map<String, CartItem>) session.getAttribute("cart");
            
            if (cart == null || cart.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cart is empty!");
                String lang = request.getParameter("lang");
                return "redirect:/cart" + (lang != null ? "?lang=" + lang : "");
            }
            
            // Create order DTO
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setClientEmail(authentication.getName());
            orderDTO.setOrderDate(LocalDateTime.now());
            // Employee will be null for pending approval
            orderDTO.setEmployeeEmail(null);
            
            // Calculate total and create book items
            BigDecimal total = BigDecimal.ZERO;
            List<BookItemDTO> bookItems = new ArrayList<>();
            
            for (CartItem cartItem : cart.values()) {
                BookItemDTO bookItemDTO = new BookItemDTO();
                // Create a Book entity with just the ID - OrderService will fetch the full entity
                BookDTO bookDTO = cartItem.getBook();
                if (bookDTO.getId() == null) {
                    throw new RuntimeException("Book ID is missing in cart item: " + bookDTO.getName());
                }
                
                Book book = new Book();
                book.setId(bookDTO.getId());
                book.setName(bookDTO.getName()); // Keep name for logging/debugging
                bookItemDTO.setBook(book);
                bookItemDTO.setQuantity(cartItem.getQuantity());
                bookItems.add(bookItemDTO);
                
                total = total.add(cartItem.getSubtotal());
            }
            
            orderDTO.setPrice(total);
            orderDTO.setBookItems(bookItems);
            
            // Create the order
            orderService.addOrder(orderDTO);
            
            // Clear the cart
            session.removeAttribute("cart");
            
            redirectAttributes.addFlashAttribute("success", "Order created successfully! Waiting for approval.");
            
            String lang = request.getParameter("lang");
            return "redirect:/orders" + (lang != null ? "?lang=" + lang : "");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create order: " + e.getMessage());
            String lang = request.getParameter("lang");
            return "redirect:/cart" + (lang != null ? "?lang=" + lang : "");
        }
    }
}