package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/client/{email}")
    @PreAuthorize("hasRole('EMPLOYEE') or #email == authentication.principal.username")
    public ResponseEntity<List<OrderDTO>> getAllOrdersByClient(@PathVariable String email) {
        return ResponseEntity.ok(orderService.getOrdersByClient(email));
    }

    @GetMapping("/employee/{email}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<OrderDTO>> getAllOrdersByEmployee(@PathVariable String email) {
        return ResponseEntity.ok(orderService.getOrdersByEmployee(email));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderDTO> addOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(orderDTO));
    }
}
