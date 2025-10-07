package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private Employee employee;
    private Client client;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("employee@email.com");
        employee.setPassword("encodedEmployeePassword");
        employee.setName("John Employee");
        employee.setPhone("123-456-7890");
        employee.setBirthDate(LocalDate.of(1990, 1, 1));

        client = new Client();
        client.setId(2L);
        client.setEmail("client@email.com");
        client.setPassword("encodedClientPassword");
        client.setName("Jane Client");
        client.setBalance(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Should load employee user details successfully")
    void testLoadUserByUsernameEmployee() {
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(employee.getEmail());

        assertNotNull(userDetails);
        assertEquals(employee.getEmail(), userDetails.getUsername());
        assertEquals(employee.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        verify(employeeRepository).findByEmail(employee.getEmail());
        verify(clientRepository, never()).findByEmail(employee.getEmail());
    }

    @Test
    @DisplayName("Should load client user details successfully")
    void testLoadUserByUsernameClient() {
        when(employeeRepository.findByEmail(client.getEmail())).thenReturn(Optional.empty());
        when(clientRepository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(client.getEmail());

        assertNotNull(userDetails);
        assertEquals(client.getEmail(), userDetails.getUsername());
        assertEquals(client.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT")));
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        verify(employeeRepository).findByEmail(client.getEmail());
        verify(clientRepository).findByEmail(client.getEmail());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void testLoadUserByUsernameNotFound() {
        String nonExistentEmail = "nonexistent@email.com";
        
        when(employeeRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());
        when(clientRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(nonExistentEmail);
        });

        verify(employeeRepository).findByEmail(nonExistentEmail);
        verify(clientRepository).findByEmail(nonExistentEmail);
    }

    @Test
    @DisplayName("Should prioritize employee over client when both exist with same email")
    void testLoadUserByUsernamePrioritizeEmployee() {
        String sharedEmail = "shared@email.com";
        employee.setEmail(sharedEmail);
        client.setEmail(sharedEmail);

        when(employeeRepository.findByEmail(sharedEmail)).thenReturn(Optional.of(employee));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(sharedEmail);

        assertNotNull(userDetails);
        assertEquals(employee.getEmail(), userDetails.getUsername());
        assertEquals(employee.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
        verify(employeeRepository).findByEmail(sharedEmail);
        verify(clientRepository, never()).findByEmail(sharedEmail);
    }

    @Test
    @DisplayName("Should handle null email gracefully")
    void testLoadUserByUsernameNullEmail() {
        when(employeeRepository.findByEmail(null)).thenReturn(Optional.empty());
        when(clientRepository.findByEmail(null)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(null);
        });

        verify(employeeRepository).findByEmail(null);
        verify(clientRepository).findByEmail(null);
    }

    @Test
    @DisplayName("Should handle empty email gracefully")
    void testLoadUserByUsernameEmptyEmail() {
        String emptyEmail = "";
        
        when(employeeRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());
        when(clientRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(emptyEmail);
        });

        verify(employeeRepository).findByEmail(emptyEmail);
        verify(clientRepository).findByEmail(emptyEmail);
    }
}