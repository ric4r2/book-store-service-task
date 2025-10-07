package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderDTO testOrderDTO;
    private Client testClient;
    private Employee testEmployee;
    private Book testBook;
    private BookItem testBookItem;
    private BookItemDTO testBookItemDTO;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setEmail("client@test.com");
        testClient.setName("Test Client");

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setEmail("employee@test.com");
        testEmployee.setName("Test Employee");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setName("Test Book");

        testBookItem = new BookItem();
        testBookItem.setId(1L);
        testBookItem.setBook(testBook);
        testBookItem.setQuantity(2);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setClient(testClient);
        testOrder.setEmployee(testEmployee);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setPrice(BigDecimal.valueOf(59.98));
        testOrder.setBookItems(Arrays.asList(testBookItem));

        testBookItemDTO = new BookItemDTO();
        testBookItemDTO.setBook(testBook);
        testBookItemDTO.setQuantity(2);

        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setClientEmail("client@test.com");
        testOrderDTO.setEmployeeEmail("employee@test.com");
        testOrderDTO.setOrderDate(LocalDateTime.now());
        testOrderDTO.setPrice(BigDecimal.valueOf(59.98));
        testOrderDTO.setBookItems(Arrays.asList(testBookItemDTO));
    }

    @Test
    @DisplayName("Should get orders by client successfully")
    void testGetOrdersByClient() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAllByClientEmail("client@test.com")).thenReturn(orders);
        when(modelMapper.map(testBookItem, BookItemDTO.class)).thenReturn(testBookItemDTO);

        List<OrderDTO> result = orderService.getOrdersByClient("client@test.com");

        assertEquals(1, result.size());
        assertEquals("client@test.com", result.get(0).getClientEmail());
        verify(orderRepository).findAllByClientEmail("client@test.com");
    }

    @Test
    @DisplayName("Should return empty list when client has no orders")
    void testGetOrdersByClientEmpty() {
        when(orderRepository.findAllByClientEmail("client@test.com")).thenReturn(Collections.emptyList());

        List<OrderDTO> result = orderService.getOrdersByClient("client@test.com");

        assertEquals(0, result.size());
        verify(orderRepository).findAllByClientEmail("client@test.com");
    }

    @Test
    @DisplayName("Should get orders by employee successfully")
    void testGetOrdersByEmployee() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAllByEmployeeEmail("employee@test.com")).thenReturn(orders);
        when(modelMapper.map(testBookItem, BookItemDTO.class)).thenReturn(testBookItemDTO);

        List<OrderDTO> result = orderService.getOrdersByEmployee("employee@test.com");

        assertEquals(1, result.size());
        assertEquals("employee@test.com", result.get(0).getEmployeeEmail());
        verify(orderRepository).findAllByEmployeeEmail("employee@test.com");
    }

    @Test
    @DisplayName("Should return empty list when employee has no orders")
    void testGetOrdersByEmployeeEmpty() {
        when(orderRepository.findAllByEmployeeEmail("employee@test.com")).thenReturn(Collections.emptyList());

        List<OrderDTO> result = orderService.getOrdersByEmployee("employee@test.com");

        assertEquals(0, result.size());
        verify(orderRepository).findAllByEmployeeEmail("employee@test.com");
    }

    @Test
    @DisplayName("Should add order successfully")
    void testAddOrderSuccess() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.of(testClient));
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(Optional.of(testEmployee));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(modelMapper.map(testBookItem, BookItemDTO.class)).thenReturn(testBookItemDTO);

        OrderDTO result = orderService.addOrder(testOrderDTO);

        assertEquals("client@test.com", result.getClientEmail());
        assertEquals("employee@test.com", result.getEmployeeEmail());
        verify(clientRepository).findByEmail("client@test.com");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should add order without employee successfully")
    void testAddOrderWithoutEmployee() {
        testOrderDTO.setEmployeeEmail(null);
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.of(testClient));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(modelMapper.map(testBookItem, BookItemDTO.class)).thenReturn(testBookItemDTO);

        OrderDTO result = orderService.addOrder(testOrderDTO);

        assertEquals("client@test.com", result.getClientEmail());
        verify(clientRepository).findByEmail("client@test.com");
        verify(employeeRepository, never()).findByEmail(anyString());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when client not found")
    void testAddOrderClientNotFound() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            orderService.addOrder(testOrderDTO);
        });

        verify(clientRepository).findByEmail("client@test.com");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when employee not found")
    void testAddOrderEmployeeNotFound() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.of(testClient));
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            orderService.addOrder(testOrderDTO);
        });

        verify(clientRepository).findByEmail("client@test.com");
        verify(employeeRepository).findByEmail("employee@test.com");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void testAddOrderBookNotFound() {
        when(clientRepository.findByEmail("client@test.com")).thenReturn(Optional.of(testClient));
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(Optional.of(testEmployee));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            orderService.addOrder(testOrderDTO);
        });

        verify(bookRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void testGetAllOrders() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);
        when(modelMapper.map(testBookItem, BookItemDTO.class)).thenReturn(testBookItemDTO);

        List<OrderDTO> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals("client@test.com", result.get(0).getClientEmail());
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void testGetAllOrdersEmpty() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderDTO> result = orderService.getAllOrders();

        assertEquals(0, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should approve order successfully")
    void testApproveOrderSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(Optional.of(testEmployee));
        when(orderRepository.save(testOrder)).thenReturn(testOrder);
        when(modelMapper.map(testBookItem, BookItemDTO.class)).thenReturn(testBookItemDTO);

        OrderDTO result = orderService.approveOrder(1L, "employee@test.com");

        assertEquals("employee@test.com", result.getEmployeeEmail());
        verify(orderRepository).findById(1L);
        verify(employeeRepository).findByEmail("employee@test.com");
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("Should throw exception when order not found for approval")
    void testApproveOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            orderService.approveOrder(1L, "employee@test.com");
        });

        verify(orderRepository).findById(1L);
        verify(employeeRepository, never()).findByEmail(anyString());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when employee not found for approval")
    void testApproveOrderEmployeeNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(employeeRepository.findByEmail("employee@test.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            orderService.approveOrder(1L, "employee@test.com");
        });

        verify(orderRepository).findById(1L);
        verify(employeeRepository).findByEmail("employee@test.com");
        verify(orderRepository, never()).save(any(Order.class));
    }
}
