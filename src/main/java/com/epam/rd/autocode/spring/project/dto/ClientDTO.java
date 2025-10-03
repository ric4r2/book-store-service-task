package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    @NotBlank(message = "{validation.email.notblank}")
    @Email(message = "{validation.email.invalid}")
    private String email;
    
    @NotBlank(message = "{validation.password.notblank}")
    @Size(min = 6, message = "{validation.password.size}")
    private String password;
    
    @NotBlank(message = "{validation.name.notblank}")
    private String name;
    
    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;
}
