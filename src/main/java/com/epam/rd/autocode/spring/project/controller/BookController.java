package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{name}")
    public BookDTO getBookByName(@PathVariable String name) {
        return bookService.getBookByName(name);
    }

    @PostMapping
    public BookDTO addBook(@RequestBody BookDTO bookDTO) {
        return bookService.addBook(bookDTO);
    }

    @PutMapping("/{name}")
    public BookDTO updateBookByName(@PathVariable String name, @RequestBody BookDTO bookDTO) {
        return bookService.updateBookByName(name, bookDTO);
    }

    @DeleteMapping("/{name}")
    public void deleteBookByName(@PathVariable String name) {
        bookService.deleteBookByName(name);
    }
}
