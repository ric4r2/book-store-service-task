package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    @NotBlank(message = "{validation.email.notblank}")
    @Email(message = "{validation.email.invalid}")
    private String email;
    
    @NotBlank(message = "{validation.password.notblank}")
    @Size(min = 6, message = "{validation.password.size}")
    private String password;
    
    @NotBlank(message = "{validation.name.notblank}")
    private String name;
    
    private String phone;
    
    private LocalDate birthDate;
}
