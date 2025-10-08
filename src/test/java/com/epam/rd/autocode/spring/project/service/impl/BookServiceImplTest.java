package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setName("Test Book");
        book.setGenre("Fiction");
        book.setAgeGroup(AgeGroup.ADULT);
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setPublicationDate(LocalDate.of(2023, 1, 1));
        book.setAuthor("Test Author");
        book.setPages(250);
        book.setCharacteristics("Hardcover");
        book.setDescription("A test book");
        book.setLanguage(Language.ENGLISH);

        bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setName("Test Book");
        bookDTO.setGenre("Fiction");
        bookDTO.setAgeGroup(AgeGroup.ADULT);
        bookDTO.setPrice(BigDecimal.valueOf(19.99));
        bookDTO.setPublicationDate(LocalDate.of(2023, 1, 1));
        bookDTO.setAuthor("Test Author");
        bookDTO.setPages(250);
        bookDTO.setCharacteristics("Hardcover");
        bookDTO.setDescription("A test book");
        bookDTO.setLanguage(Language.ENGLISH);
    }

    @Test
    @DisplayName("Should return all books successfully")
    void testGetAllBooks() {
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findAll()).thenReturn(books);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        List<BookDTO> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO.getName(), result.get(0).getName());
        verify(bookRepository).findAll();
        verify(modelMapper).map(book, BookDTO.class);
    }

    @Test
    @DisplayName("Should return empty list when no books found")
    void testGetAllBooksEmpty() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList());

        List<BookDTO> result = bookService.getAllBooks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Should return book by name successfully")
    void testGetBookByNameSuccess() {
        when(bookRepository.findByName(book.getName())).thenReturn(Optional.of(book));
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        BookDTO result = bookService.getBookByName(book.getName());

        assertNotNull(result);
        assertEquals(bookDTO.getName(), result.getName());
        assertEquals(bookDTO.getAuthor(), result.getAuthor());
        verify(bookRepository).findByName(book.getName());
        verify(modelMapper).map(book, BookDTO.class);
    }

    @Test
    @DisplayName("Should throw AlreadyExistException when book not found by name")
    void testGetBookByNameNotFound() {
        when(bookRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(AlreadyExistException.class, () -> {
            bookService.getBookByName("Nonexistent Book");
        });

        verify(bookRepository).findByName("Nonexistent Book");
    }

    @Test
    @DisplayName("Should update book by name successfully")
    void testUpdateBookByNameSuccess() {
        BookDTO updateDTO = new BookDTO();
        updateDTO.setName("Test Book");
        updateDTO.setGenre("Updated Fiction");
        updateDTO.setAgeGroup(AgeGroup.TEEN);
        updateDTO.setPrice(BigDecimal.valueOf(24.99));
        updateDTO.setPublicationDate(LocalDate.of(2024, 1, 1));
        updateDTO.setAuthor("Updated Author");
        updateDTO.setPages(300);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setName("Test Book");
        updatedBook.setGenre("Updated Fiction");
        updatedBook.setAgeGroup(AgeGroup.TEEN);
        updatedBook.setPrice(BigDecimal.valueOf(24.99));

        BookDTO resultDTO = new BookDTO();
        resultDTO.setName("Test Book");
        resultDTO.setGenre("Updated Fiction");
        resultDTO.setAgeGroup(AgeGroup.TEEN);
        resultDTO.setPrice(BigDecimal.valueOf(24.99));

        when(bookRepository.findByName(updateDTO.getName())).thenReturn(Optional.of(book));
        doNothing().when(modelMapper).map(updateDTO, book);
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(modelMapper.map(updatedBook, BookDTO.class)).thenReturn(resultDTO);

        BookDTO result = bookService.updateBookByName(updateDTO.getName(), updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO.getName(), result.getName());
        assertEquals(updateDTO.getGenre(), result.getGenre());
        verify(bookRepository).findByName(updateDTO.getName());
        verify(modelMapper).map(updateDTO, book);
        verify(bookRepository).save(book);
        verify(modelMapper).map(updatedBook, BookDTO.class);
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent book")
    void testUpdateBookByNameNotFound() {
        when(bookRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookService.updateBookByName("Nonexistent Book", bookDTO);
        });

        verify(bookRepository).findByName("Nonexistent Book");
    }

    @Test
    @DisplayName("Should throw AlreadyExistException when updating to existing book name")
    void testUpdateBookByNameAlreadyExists() {
        BookDTO updateDTO = new BookDTO();
        updateDTO.setName("Different Book Name");
        updateDTO.setGenre("Fiction");

        when(bookRepository.findByName("Test Book")).thenReturn(Optional.of(book));
        when(bookRepository.existsByName("Different Book Name")).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> {
            bookService.updateBookByName("Test Book", updateDTO);
        });

        verify(bookRepository).findByName("Test Book");
        verify(bookRepository).existsByName("Different Book Name");
    }

    @Test
    @DisplayName("Should delete book by name successfully")
    void testDeleteBookByNameSuccess() {
        when(bookRepository.existsByName(book.getName())).thenReturn(true);

        bookService.deleteBookByName(book.getName());

        verify(bookRepository).existsByName(book.getName());
        verify(bookRepository).deleteByName(book.getName());
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existent book")
    void testDeleteBookByNameNotFound() {
        when(bookRepository.existsByName(anyString())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> {
            bookService.deleteBookByName("Nonexistent Book");
        });

        verify(bookRepository).existsByName("Nonexistent Book");
        verify(bookRepository, never()).deleteByName(anyString());
    }

    @Test
    @DisplayName("Should add new book successfully")
    void testAddBookSuccess() {
        Book savedBook = new Book();
        savedBook.setId(2L);
        savedBook.setName("New Book");
        savedBook.setGenre("Mystery");
        savedBook.setAuthor("New Author");

        BookDTO newBookDTO = new BookDTO();
        newBookDTO.setName("New Book");
        newBookDTO.setGenre("Mystery");
        newBookDTO.setAuthor("New Author");
        newBookDTO.setAgeGroup(AgeGroup.ADULT);
        newBookDTO.setPrice(BigDecimal.valueOf(15.99));
        newBookDTO.setPublicationDate(LocalDate.of(2024, 6, 1));
        newBookDTO.setPages(200);
        newBookDTO.setLanguage(Language.ENGLISH);

        BookDTO resultDTO = new BookDTO();
        resultDTO.setId(2L);
        resultDTO.setName("New Book");
        resultDTO.setGenre("Mystery");
        resultDTO.setAuthor("New Author");

        when(bookRepository.existsByName(newBookDTO.getName())).thenReturn(false);
        when(modelMapper.map(newBookDTO, Book.class)).thenReturn(savedBook);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);
        when(modelMapper.map(savedBook, BookDTO.class)).thenReturn(resultDTO);

        BookDTO result = bookService.addBook(newBookDTO);

        assertNotNull(result);
        assertEquals(newBookDTO.getName(), result.getName());
        assertEquals(newBookDTO.getGenre(), result.getGenre());
        verify(bookRepository).existsByName(newBookDTO.getName());
        verify(modelMapper).map(newBookDTO, Book.class);
        verify(bookRepository).save(any(Book.class));
        verify(modelMapper).map(savedBook, BookDTO.class);
    }

    @Test
    @DisplayName("Should throw AlreadyExistException when adding existing book")
    void testAddBookAlreadyExists() {
        when(bookRepository.existsByName(bookDTO.getName())).thenReturn(true);

        assertThrows(AlreadyExistException.class, () -> {
            bookService.addBook(bookDTO);
        });

        verify(bookRepository).existsByName(bookDTO.getName());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should return paginated books with search successfully")
    void testGetBooksWithSearch() {
        String search = "fiction";
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDirection = "asc";

        List<Book> books = Arrays.asList(book);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy)), 1);
        
        when(bookRepository.findBooksWithSearch(eq(search), any(Pageable.class))).thenReturn(bookPage);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        Page<BookDTO> result = bookService.getBooks(search, page, size, sortBy, sortDirection);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(bookDTO.getName(), result.getContent().get(0).getName());
        verify(bookRepository).findBooksWithSearch(eq(search), any(Pageable.class));
    }

    @Test
    @DisplayName("Should use default sort parameters when null or empty")
    void testGetBooksWithDefaultSort() {
        String search = "test";
        int page = 0;
        int size = 5;
        String sortBy = null;
        String sortDirection = "";

        List<Book> books = Arrays.asList(book);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name")), 1);
        
        when(bookRepository.findBooksWithSearch(eq(search), any(Pageable.class))).thenReturn(bookPage);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        Page<BookDTO> result = bookService.getBooks(search, page, size, sortBy, sortDirection);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository).findBooksWithSearch(eq(search), any(Pageable.class));
    }

    @Test
    @DisplayName("Should handle descending sort direction")
    void testGetBooksWithDescendingSort() {
        String search = "test";
        int page = 0;
        int size = 5;
        String sortBy = "price";
        String sortDirection = "desc";

        List<Book> books = Arrays.asList(book);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy)), 1);
        
        when(bookRepository.findBooksWithSearch(eq(search), any(Pageable.class))).thenReturn(bookPage);
        when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        Page<BookDTO> result = bookService.getBooks(search, page, size, sortBy, sortDirection);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookRepository).findBooksWithSearch(eq(search), any(Pageable.class));
    }
}
