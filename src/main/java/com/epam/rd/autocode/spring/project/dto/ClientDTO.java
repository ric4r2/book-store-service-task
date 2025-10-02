package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientDTO {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal balance;
}
