package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        log.debug("Fetching all books");
        return bookRepository.findAll()
                .stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookByName(String name) {
        log.debug("Fetching book by name: {}", name);
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new AlreadyExistException("Book not found with name: " + name));
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO bookDTO) {
        log.debug("Updating book with name: {}", name);
        Book book = bookRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Book not found with name: " + name));

        if (!name.equals(bookDTO.getName()) && bookRepository.existsByName(bookDTO.getName())) {
            throw new AlreadyExistException("Book already exists with name: " + bookDTO.getName());
        }

        modelMapper.map(bookDTO, book);
        book = bookRepository.save(book);
        log.info("Book updated successfully: {}", name);
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public void deleteBookByName(String name) {
        log.debug("Deleting book with name: {}", name);
        if (!bookRepository.existsByName(name)) {
            throw new NotFoundException("Book not found with name: " + name);
        }
        bookRepository.deleteByName(name);
        log.info("Book deleted successfully: {}", name);
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        log.debug("Adding new book: {}", bookDTO.getName());
        if (bookRepository.existsByName(bookDTO.getName())) {
            throw new AlreadyExistException("Book already exists with name: " + bookDTO.getName());
        }

        Book book = modelMapper.map(bookDTO, Book.class);
        book = bookRepository.save(book);
        log.info("Book added successfully: {}", book.getName());
        return modelMapper.map(book, BookDTO.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getBooks(String search, int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching books with search: '{}', page: {}, size: {}, sortBy: {}, direction: {}", 
                  search, page, size, sortBy, sortDirection);
        
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "name";
        }
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            sortDirection = "asc";
        }
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> bookPage = bookRepository.findBooksWithSearch(search, pageable);
        
        return bookPage.map(book -> modelMapper.map(book, BookDTO.class));
    }
}