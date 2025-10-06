package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{name}")
    public ResponseEntity<BookDTO> getBookByName(@PathVariable String name) {
        return ResponseEntity.ok(bookService.getBookByName(name));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(bookDTO));
    }

    @PutMapping("/{name}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BookDTO> updateBookByName(@PathVariable String name, @Valid @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.updateBookByName(name, bookDTO));
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> deleteBookByName(@PathVariable String name) {
        bookService.deleteBookByName(name);
        return ResponseEntity.noContent().build();
    }
}
