package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        log.debug("Fetching all employees");
        return employeeRepository.findAll()
                .stream()
                .map(employee -> {
                    EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
                    dto.setPassword(null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDTO getEmployeeByEmail(String email) {
        log.debug("Fetching employee by email: {}", email);
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found with email: " + email));
        EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
        dto.setPassword(null);
        return dto;
    }

    @Override
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employeeDTO) {
        log.debug("Updating employee with email: {}", email);
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Employee not found with email: " + email));

        if (!email.equals(employeeDTO.getEmail()) && employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
            throw new AlreadyExistException("Employee already exists with email: " + employeeDTO.getEmail());
        }

        employee.setEmail(employeeDTO.getEmail());
        employee.setName(employeeDTO.getName());
        employee.setPhone(employeeDTO.getPhone());
        employee.setBirthDate(employeeDTO.getBirthDate());

        if (employeeDTO.getPassword() != null && !employeeDTO.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        }

        employee = employeeRepository.save(employee);
        log.info("Employee updated successfully: {}", email);
        EmployeeDTO resultDto = modelMapper.map(employee, EmployeeDTO.class);
        resultDto.setPassword(null);
        return resultDto;
    }

    @Override
    public void deleteEmployeeByEmail(String email) {
        log.debug("Deleting employee with email: {}", email);
        if (employeeRepository.findByEmail(email).isEmpty()) {
            throw new NotFoundException("Employee not found with email: " + email);
        }
        employeeRepository.deleteByEmail(email);
        log.info("Employee deleted successfully: {}", email);
    }

    @Override
    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        log.debug("Adding new employee: {}", employeeDTO.getEmail());
        if (employeeRepository.findByEmail(employeeDTO.getEmail()).isPresent()) {
            throw new AlreadyExistException("Employee already exists with email: " + employeeDTO.getEmail());
        }

        Employee employee = modelMapper.map(employeeDTO, Employee.class);
        employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        employee = employeeRepository.save(employee);
        log.info("Employee added successfully: {}", employee.getEmail());
        EmployeeDTO resultDto = modelMapper.map(employee, EmployeeDTO.class);
        resultDto.setPassword(null);
        return resultDto;
    }
}