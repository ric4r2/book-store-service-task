package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.Book;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookItemDTO {
    @NotNull
    private Book book;
    @NotNull
    @Min(1)
    private Integer quantity;
}
