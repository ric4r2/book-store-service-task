package com.epam.rd.autocode.spring.project.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    @Email
    @NotBlank
    private String clientEmail;
    @Email
    @NotBlank
    private String employeeEmail;
    @NotNull
    private LocalDateTime orderDate;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
    @NotNull
    private List<BookItemDTO> bookItems;
}
