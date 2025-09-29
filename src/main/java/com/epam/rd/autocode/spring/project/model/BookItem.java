package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "book_items")
@Data
public class BookItem {
    @Id
    private Long id;
    private Order order;
    private Book book;
    private Integer quantity;
}
