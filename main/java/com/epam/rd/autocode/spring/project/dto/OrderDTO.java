package com.epam.rd.autocode.spring.project.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    
    @NotNull(message = "{validation.client.email.notnull}")
    @Email(message = "{validation.email.invalid}")
    private String clientEmail;
    
    private String employeeEmail;
    
    @NotNull
    private LocalDateTime orderDate;
    
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;
    
    @NotEmpty(message = "{validation.order.items.notempty}")
    private List<BookItemDTO> bookItems;
}
