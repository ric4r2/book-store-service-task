package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/client/{email}")
    public List<OrderDTO> getOrdersByClient(@PathVariable String email) {
        return orderService.getOrdersByClient(email);
    }

    @GetMapping("/employee/{email}")
    public List<OrderDTO> getOrdersByEmployee(@PathVariable String email) {
        return orderService.getOrdersByEmployee(email);
    }

    @PostMapping
    public OrderDTO addOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.addOrder(orderDTO);
    }
}
