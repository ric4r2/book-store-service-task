package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private ClientService clientService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testHome() {
        String viewName = homeController.home(model);

        assertEquals("index", viewName);
        verify(model).addAttribute("totalBooks", "12,500+");
        verify(model).addAttribute("totalCustomers", "8,200+");
        verify(model).addAttribute("totalOrders", "35,000+");
    }

    @Test
    void testBooks() {
        @SuppressWarnings("unchecked")
        Page<Object> mockPage = mock(Page.class);
        doReturn(mockPage).when(bookService).getBooks("", 0, 15, "name", "asc");

        String viewName = homeController.books(model, "", 0, 15, "name", "asc");

        assertEquals("books", viewName);
        verify(bookService).getBooks("", 0, 15, "name", "asc");
    }

    @Test
    void testBooksWithException() {
        when(bookService.getBooks("", 0, 15, "name", "asc")).thenThrow(new RuntimeException("Error"));

        String viewName = homeController.books(model, "", 0, 15, "name", "asc");

        assertEquals("books", viewName);
        verify(model).addAttribute("books", Collections.emptyList());
        verify(model).addAttribute("error", "Unable to load books at this time.");
    }

    @Test
    void testProfileForClient() {
        String email = "client@test.com";
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail(email);
        
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
        when(authentication.getName()).thenReturn(email);
        doReturn(authorities).when(authentication).getAuthorities();
        when(clientService.getClientByEmail(email)).thenReturn(clientDTO);

        String viewName = homeController.profile(model, authentication);

        assertEquals("profile", viewName);
        verify(clientService).getClientByEmail(email);
        verify(model).addAttribute("user", clientDTO);
        verify(model).addAttribute("isEmployee", false);
    }

    @Test
    void testProfileForEmployee() {
        String email = "employee@test.com";
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail(email);
        
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        when(authentication.getName()).thenReturn(email);
        doReturn(authorities).when(authentication).getAuthorities();
        when(employeeService.getEmployeeByEmail(email)).thenReturn(employeeDTO);

        String viewName = homeController.profile(model, authentication);

        assertEquals("profile", viewName);
        verify(employeeService).getEmployeeByEmail(email);
        verify(model).addAttribute("user", employeeDTO);
        verify(model).addAttribute("isEmployee", true);
    }

    @Test
    void testLogin() {
        String viewName = homeController.login();

        assertEquals("login", viewName);
    }

    @Test
    void testRegister() {
        String viewName = homeController.register();

        assertEquals("register", viewName);
    }
}