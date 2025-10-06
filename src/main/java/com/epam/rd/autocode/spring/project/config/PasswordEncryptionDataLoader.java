package com.epam.rd.autocode.spring.project.config;

import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordEncryptionDataLoader implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting password encryption for existing users...");
        
        employeeRepository.findAll().forEach(employee -> {
            if (!isPasswordEncrypted(employee.getPassword())) {
                String originalPassword = employee.getPassword();
                employee.setPassword(passwordEncoder.encode(originalPassword));
                employeeRepository.save(employee);
                log.debug("Encrypted password for employee: {}", employee.getEmail());
            }
        });
        
        clientRepository.findAll().forEach(client -> {
            if (!isPasswordEncrypted(client.getPassword())) {
                String originalPassword = client.getPassword();
                client.setPassword(passwordEncoder.encode(originalPassword));
                clientRepository.save(client);
                log.debug("Encrypted password for client: {}", client.getEmail());
            }
        });
        
        log.info("Password encryption completed.");
    }
    
    private boolean isPasswordEncrypted(String password) {
        return password != null && password.length() == 60 && password.startsWith("$2");
    }
}