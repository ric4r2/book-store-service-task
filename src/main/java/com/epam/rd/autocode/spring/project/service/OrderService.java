package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;

import java.util.*;

public interface OrderService {

    List<OrderDTO> getOrdersByClient(String clientEmail);

    List<OrderDTO> getOrdersByEmployee(String employeeEmail);

    OrderDTO addOrder(OrderDTO order);
    
    List<OrderDTO> getAllOrders();
    
    OrderDTO approveOrder(Long orderId, String employeeEmail);
}
