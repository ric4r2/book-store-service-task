package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    private Long id;
    private Client client;
    private Employee employee;
    private LocalDateTime orderDate;
    private BigDecimal price;
    private List<BookItem> bookItems;
}
