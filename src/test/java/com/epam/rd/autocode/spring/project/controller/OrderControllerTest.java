package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testOrdersForEmployee() {
        String email = "employee@test.com";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        
        when(authentication.getName()).thenReturn(email);
        doReturn(authorities).when(authentication).getAuthorities();
        when(orderService.getAllOrders()).thenReturn(new ArrayList<>());

        String result = orderController.orders(model, authentication);

        assertEquals("orders", result);
        verify(orderService).getAllOrders();
        verify(model).addAttribute("orders", new ArrayList<>());
        verify(model).addAttribute("isEmployee", true);
    }

    @Test
    void testOrdersForClient() {
        String email = "client@test.com";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
        
        when(authentication.getName()).thenReturn(email);
        doReturn(authorities).when(authentication).getAuthorities();
        when(orderService.getOrdersByClient(email)).thenReturn(new ArrayList<>());

        String result = orderController.orders(model, authentication);

        assertEquals("orders", result);
        verify(orderService).getOrdersByClient(email);
        verify(model).addAttribute("orders", new ArrayList<>());
        verify(model).addAttribute("isEmployee", false);
    }

    @Test
    void testOrdersWithException() {
        String email = "client@test.com";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
        
        when(authentication.getName()).thenReturn(email);
        doReturn(authorities).when(authentication).getAuthorities();
        when(orderService.getOrdersByClient(email)).thenThrow(new RuntimeException("Error"));

        String result = orderController.orders(model, authentication);

        assertEquals("orders", result);
        verify(model).addAttribute("orders", Collections.emptyList());
        verify(model).addAttribute("error", "Unable to load orders at this time.");
        verify(model).addAttribute("isEmployee", false);
    }

    @Test
    void testApproveOrder() {
        Long orderId = 1L;
        String employeeEmail = "employee@test.com";
        
        when(authentication.getName()).thenReturn(employeeEmail);

        String result = orderController.approveOrder(orderId, authentication, redirectAttributes);

        assertEquals("redirect:/orders", result);
        verify(orderService).approveOrder(orderId, employeeEmail);
        verify(redirectAttributes).addFlashAttribute("success", "Order approved successfully!");
    }

    @Test
    void testApproveOrderWithException() {
        Long orderId = 1L;
        String employeeEmail = "employee@test.com";
        
        when(authentication.getName()).thenReturn(employeeEmail);
        doThrow(new RuntimeException("Error")).when(orderService).approveOrder(orderId, employeeEmail);

        String result = orderController.approveOrder(orderId, authentication, redirectAttributes);

        assertEquals("redirect:/orders", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Failed to approve order"));
    }
}