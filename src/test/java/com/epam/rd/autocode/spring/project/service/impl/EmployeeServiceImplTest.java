package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("employee@email.com");
        employee.setName("John Doe");
        employee.setPassword("encodedPassword");
        employee.setPhone("123-456-7890");
        employee.setBirthDate(LocalDate.of(1990, 1, 1));

        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail("employee@email.com");
        employeeDTO.setName("John Doe");
        employeeDTO.setPassword("plainPassword");
        employeeDTO.setPhone("123-456-7890");
        employeeDTO.setBirthDate(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Should return all employees successfully")
    void testGetAllEmployees() {
        List<Employee> employees = Arrays.asList(employee);
        EmployeeDTO dtoWithoutPassword = new EmployeeDTO();
        dtoWithoutPassword.setEmail("employee@email.com");
        dtoWithoutPassword.setName("John Doe");
        dtoWithoutPassword.setPhone("123-456-7890");
        dtoWithoutPassword.setBirthDate(LocalDate.of(1990, 1, 1));

        when(employeeRepository.findAll()).thenReturn(employees);
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(employeeDTO.getEmail(), result.get(0).getEmail());
        assertNull(result.get(0).getPassword());
        verify(employeeRepository).findAll();
        verify(modelMapper).map(employee, EmployeeDTO.class);
    }

    @Test
    @DisplayName("Should return empty list when no employees found")
    void testGetAllEmployeesEmpty() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList());

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository).findAll();
    }

    @Test
    @DisplayName("Should return employee by email successfully")
    void testGetEmployeeByEmailSuccess() {
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(modelMapper.map(employee, EmployeeDTO.class)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployeeByEmail(employee.getEmail());

        assertNotNull(result);
        assertEquals(employeeDTO.getEmail(), result.getEmail());
        assertEquals(employeeDTO.getName(), result.getName());
        assertNull(result.getPassword());
        verify(employeeRepository).findByEmail(employee.getEmail());
        verify(modelMapper).map(employee, EmployeeDTO.class);
    }

    @Test
    @DisplayName("Should throw NotFoundException when employee not found by email")
    void testGetEmployeeByEmailNotFound() {
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.getEmployeeByEmail("nonexistent@email.com");
        });

        verify(employeeRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    @DisplayName("Should update employee by email successfully")
    void testUpdateEmployeeByEmailSuccess() {
        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setEmail("employee@email.com");
        updateDTO.setName("Jane Smith");
        updateDTO.setPassword("newPassword");
        updateDTO.setPhone("987-654-3210");
        updateDTO.setBirthDate(LocalDate.of(1985, 5, 15));

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setEmail("employee@email.com");
        updatedEmployee.setName("Jane Smith");
        updatedEmployee.setPassword("encodedNewPassword");
        updatedEmployee.setPhone("987-654-3210");
        updatedEmployee.setBirthDate(LocalDate.of(1985, 5, 15));

        EmployeeDTO resultDTO = new EmployeeDTO();
        resultDTO.setEmail("employee@email.com");
        resultDTO.setName("Jane Smith");
        resultDTO.setPhone("987-654-3210");
        resultDTO.setBirthDate(LocalDate.of(1985, 5, 15));

        when(employeeRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(employee));
        when(passwordEncoder.encode(updateDTO.getPassword())).thenReturn("encodedNewPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(modelMapper.map(updatedEmployee, EmployeeDTO.class)).thenReturn(resultDTO);

        EmployeeDTO result = employeeService.updateEmployeeByEmail(updateDTO.getEmail(), updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO.getEmail(), result.getEmail());
        assertEquals(updateDTO.getName(), result.getName());
        assertNull(result.getPassword());
        verify(employeeRepository).findByEmail(updateDTO.getEmail());
        verify(passwordEncoder).encode(updateDTO.getPassword());
        verify(employeeRepository).save(any(Employee.class));
        verify(modelMapper).map(updatedEmployee, EmployeeDTO.class);
    }

    @Test
    @DisplayName("Should update employee without password when password is null")
    void testUpdateEmployeeByEmailWithoutPassword() {
        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setEmail("employee@email.com");
        updateDTO.setName("Jane Smith");
        updateDTO.setPassword(null);
        updateDTO.setPhone("987-654-3210");
        updateDTO.setBirthDate(LocalDate.of(1985, 5, 15));

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setEmail("employee@email.com");
        updatedEmployee.setName("Jane Smith");
        updatedEmployee.setPassword("encodedPassword");
        updatedEmployee.setPhone("987-654-3210");
        updatedEmployee.setBirthDate(LocalDate.of(1985, 5, 15));

        EmployeeDTO resultDTO = new EmployeeDTO();
        resultDTO.setEmail("employee@email.com");
        resultDTO.setName("Jane Smith");
        resultDTO.setPhone("987-654-3210");
        resultDTO.setBirthDate(LocalDate.of(1985, 5, 15));

        when(employeeRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(modelMapper.map(updatedEmployee, EmployeeDTO.class)).thenReturn(resultDTO);

        EmployeeDTO result = employeeService.updateEmployeeByEmail(updateDTO.getEmail(), updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO.getEmail(), result.getEmail());
        assertEquals(updateDTO.getName(), result.getName());
        verify(employeeRepository).findByEmail(updateDTO.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(employeeRepository).save(any(Employee.class));
        verify(modelMapper).map(updatedEmployee, EmployeeDTO.class);
    }

    @Test
    @DisplayName("Should throw AlreadyExistException when updating to existing email")
    void testUpdateEmployeeByEmailAlreadyExists() {
        Employee existingEmployee = new Employee();
        existingEmployee.setEmail("existing@email.com");

        EmployeeDTO updateDTO = new EmployeeDTO();
        updateDTO.setEmail("existing@email.com");
        updateDTO.setName("Jane Smith");

        when(employeeRepository.findByEmail("employee@email.com")).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail("existing@email.com")).thenReturn(Optional.of(existingEmployee));

        assertThrows(AlreadyExistException.class, () -> {
            employeeService.updateEmployeeByEmail("employee@email.com", updateDTO);
        });

        verify(employeeRepository).findByEmail("employee@email.com");
        verify(employeeRepository).findByEmail("existing@email.com");
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent employee")
    void testUpdateEmployeeByEmailNotFound() {
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.updateEmployeeByEmail("nonexistent@email.com", employeeDTO);
        });

        verify(employeeRepository).findByEmail("nonexistent@email.com");
    }

    @Test
    @DisplayName("Should delete employee by email successfully")
    void testDeleteEmployeeByEmailSuccess() {
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));

        employeeService.deleteEmployeeByEmail(employee.getEmail());

        verify(employeeRepository).findByEmail(employee.getEmail());
        verify(employeeRepository).deleteByEmail(employee.getEmail());
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existent employee")
    void testDeleteEmployeeByEmailNotFound() {
        when(employeeRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.deleteEmployeeByEmail("nonexistent@email.com");
        });

        verify(employeeRepository).findByEmail("nonexistent@email.com");
        verify(employeeRepository, never()).deleteByEmail(anyString());
    }

    @Test
    @DisplayName("Should add new employee successfully")
    void testAddEmployeeSuccess() {
        Employee savedEmployee = new Employee();
        savedEmployee.setId(2L);
        savedEmployee.setEmail("new@email.com");
        savedEmployee.setName("New Employee");
        savedEmployee.setPassword("encodedPassword");
        savedEmployee.setPhone("555-555-5555");
        savedEmployee.setBirthDate(LocalDate.of(1992, 12, 25));

        EmployeeDTO newEmployeeDTO = new EmployeeDTO();
        newEmployeeDTO.setEmail("new@email.com");
        newEmployeeDTO.setName("New Employee");
        newEmployeeDTO.setPassword("plainPassword");
        newEmployeeDTO.setPhone("555-555-5555");
        newEmployeeDTO.setBirthDate(LocalDate.of(1992, 12, 25));

        EmployeeDTO resultDTO = new EmployeeDTO();
        resultDTO.setEmail("new@email.com");
        resultDTO.setName("New Employee");
        resultDTO.setPhone("555-555-5555");
        resultDTO.setBirthDate(LocalDate.of(1992, 12, 25));

        when(employeeRepository.findByEmail(newEmployeeDTO.getEmail())).thenReturn(Optional.empty());
        when(modelMapper.map(newEmployeeDTO, Employee.class)).thenReturn(savedEmployee);
        when(passwordEncoder.encode(newEmployeeDTO.getPassword())).thenReturn("encodedPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(modelMapper.map(savedEmployee, EmployeeDTO.class)).thenReturn(resultDTO);

        EmployeeDTO result = employeeService.addEmployee(newEmployeeDTO);

        assertNotNull(result);
        assertEquals(newEmployeeDTO.getEmail(), result.getEmail());
        assertEquals(newEmployeeDTO.getName(), result.getName());
        assertNull(result.getPassword());
        verify(employeeRepository).findByEmail(newEmployeeDTO.getEmail());
        verify(modelMapper).map(newEmployeeDTO, Employee.class);
        verify(passwordEncoder).encode(newEmployeeDTO.getPassword());
        verify(employeeRepository).save(any(Employee.class));
        verify(modelMapper).map(savedEmployee, EmployeeDTO.class);
    }

    @Test
    @DisplayName("Should throw AlreadyExistException when adding existing employee")
    void testAddEmployeeAlreadyExists() {
        when(employeeRepository.findByEmail(employeeDTO.getEmail())).thenReturn(Optional.of(employee));

        assertThrows(AlreadyExistException.class, () -> {
            employeeService.addEmployee(employeeDTO);
        });

        verify(employeeRepository).findByEmail(employeeDTO.getEmail());
        verify(employeeRepository, never()).save(any(Employee.class));
    }
}