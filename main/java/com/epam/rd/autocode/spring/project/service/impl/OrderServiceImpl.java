package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, ClientRepository clientRepository,
                            EmployeeRepository employeeRepository, BookRepository bookRepository,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByClient(String email) {
        log.debug("Fetching all orders for client: {}", email);
        List<Order> orders = orderRepository.findAllByClientEmail(email);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByEmployee(String email) {
        log.debug("Fetching all orders for employee: {}", email);
        List<Order> orders = orderRepository.findAllByEmployeeEmail(email);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO addOrder(OrderDTO orderDTO) {
        log.debug("Adding new order for client: {}", orderDTO.getClientEmail());

        Client client = clientRepository.findByEmail(orderDTO.getClientEmail())
                .orElseThrow(() -> new NotFoundException("Client not found with email: " + orderDTO.getClientEmail()));

        Employee employee = null;
        if (orderDTO.getEmployeeEmail() != null) {
            employee = employeeRepository.findByEmail(orderDTO.getEmployeeEmail())
                    .orElseThrow(() -> new NotFoundException("Employee not found with email: " + orderDTO.getEmployeeEmail()));
        }

        Order order = new Order();
        order.setClient(client);
        order.setEmployee(employee);
        order.setOrderDate(orderDTO.getOrderDate());
        order.setPrice(orderDTO.getPrice());

        for (BookItemDTO itemDTO : orderDTO.getBookItems()) {
            Book book = itemDTO.getBook();
            if (book.getId() != null) {
                Long bookId = book.getId();  // Store the ID in a separate final variable
                book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new NotFoundException("Book not found with id: " + bookId));
            }

            BookItem bookItem = new BookItem();
            bookItem.setBook(book);
            bookItem.setQuantity(itemDTO.getQuantity());
            bookItem.setOrder(order);
            order.getBookItems().add(bookItem);
        }

        order = orderRepository.save(order);
        log.info("Order added successfully with id: {}", order.getId());
        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setClientEmail(order.getClient().getEmail());
        if (order.getEmployee() != null) {
            dto.setEmployeeEmail(order.getEmployee().getEmail());
        }
        dto.setOrderDate(order.getOrderDate());
        dto.setPrice(order.getPrice());

        List<BookItemDTO> items = order.getBookItems().stream()
                .map(item -> modelMapper.map(item, BookItemDTO.class))
                .collect(Collectors.toList());
        dto.setBookItems(items);

        return dto;
    }
}
