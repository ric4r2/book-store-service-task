package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.controller.BookController.CartItem;
import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private OrderService orderService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testAddBook() {
        when(request.getParameter("name")).thenReturn("Test Book");
        when(request.getParameter("genre")).thenReturn("Fiction");
        when(request.getParameter("ageGroup")).thenReturn("ADULT");
        when(request.getParameter("price")).thenReturn("19.99");
        when(request.getParameter("author")).thenReturn("Test Author");
        when(request.getParameter("pages")).thenReturn("300");
        when(request.getParameter("characteristics")).thenReturn("Hardcover");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(request.getParameter("language")).thenReturn("ENGLISH");
        when(request.getParameter("publicationDate")).thenReturn("2023-01-01");
        when(request.getParameter("lang")).thenReturn("en");

        String result = bookController.addBook(request, redirectAttributes);

        assertEquals("redirect:/books?lang=en", result);
        verify(bookService).addBook(any(BookDTO.class));
        verify(redirectAttributes).addFlashAttribute("success", "Book added successfully!");
    }

    @Test
    void testAddBookWithException() {
        when(request.getParameter("name")).thenReturn("Test Book");
        when(request.getParameter("genre")).thenReturn("Fiction");
        when(request.getParameter("ageGroup")).thenReturn("ADULT");
        when(request.getParameter("price")).thenReturn("19.99");
        when(request.getParameter("author")).thenReturn("Test Author");
        when(request.getParameter("pages")).thenReturn("300");
        when(request.getParameter("characteristics")).thenReturn("Hardcover");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(request.getParameter("language")).thenReturn("ENGLISH");
        when(request.getParameter("publicationDate")).thenReturn("2023-01-01");
        when(request.getParameter("lang")).thenReturn("en");
        
        doThrow(new RuntimeException("Error")).when(bookService).addBook(any(BookDTO.class));

        String result = bookController.addBook(request, redirectAttributes);

        assertEquals("redirect:/books?lang=en", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Failed to add book"));
    }

    @Test
    void testAddToCart() {
        String bookName = "Test Book";
        Integer quantity = 2;
        BookDTO bookDTO = new BookDTO();
        bookDTO.setName(bookName);
        bookDTO.setPrice(new BigDecimal("19.99"));
        
        Map<String, CartItem> cart = new HashMap<>();
        
        when(session.getAttribute("cart")).thenReturn(cart);
        when(bookService.getBookByName(bookName)).thenReturn(bookDTO);
        when(request.getParameter("lang")).thenReturn("en");

        String result = bookController.addToCart(bookName, quantity, session, redirectAttributes, request);

        assertEquals("redirect:/books?lang=en", result);
        verify(session).setAttribute(eq("cart"), any(Map.class));
        verify(redirectAttributes).addFlashAttribute(eq("success"), contains("added to cart"));
    }

    @Test
    void testViewCart() {
        Map<String, CartItem> cart = new HashMap<>();
        BookDTO bookDTO = new BookDTO();
        bookDTO.setPrice(new BigDecimal("19.99"));
        CartItem cartItem = new CartItem(bookDTO, 2);
        cart.put("Test Book", cartItem);
        
        when(session.getAttribute("cart")).thenReturn(cart);

        String result = bookController.viewCart(session, model);

        assertEquals("cart", result);
        verify(model).addAttribute(eq("cartItems"), any());
        verify(model).addAttribute(eq("cartTotal"), any(BigDecimal.class));
        verify(model).addAttribute("cartSize", 1);
    }

    @Test
    void testViewCartEmpty() {
        when(session.getAttribute("cart")).thenReturn(null);

        String result = bookController.viewCart(session, model);

        assertEquals("cart", result);
        verify(model).addAttribute(eq("cartItems"), any());
        verify(model).addAttribute("cartTotal", BigDecimal.ZERO);
        verify(model).addAttribute("cartSize", 0);
    }

    @Test
    void testRemoveFromCart() {
        String bookName = "Test Book";
        Map<String, CartItem> cart = new HashMap<>();
        cart.put(bookName, new CartItem());
        
        when(session.getAttribute("cart")).thenReturn(cart);
        when(request.getParameter("lang")).thenReturn("en");

        String result = bookController.removeFromCart(bookName, session, redirectAttributes, request);

        assertEquals("redirect:/books/cart?lang=en", result);
        verify(session).setAttribute("cart", cart);
        verify(redirectAttributes).addFlashAttribute("success", "Item removed from cart!");
    }

    @Test
    void testUpdateCartItem() {
        String bookName = "Test Book";
        Integer quantity = 3;
        Map<String, CartItem> cart = new HashMap<>();
        cart.put(bookName, new CartItem());
        
        when(session.getAttribute("cart")).thenReturn(cart);
        when(request.getParameter("lang")).thenReturn("en");

        String result = bookController.updateCartItem(bookName, quantity, session, redirectAttributes, request);

        assertEquals("redirect:/books/cart?lang=en", result);
        verify(session).setAttribute("cart", cart);
        verify(redirectAttributes).addFlashAttribute("success", "Cart updated!");
    }

    @Test
    void testDeleteBook() {
        String bookName = "Test Book";
        when(request.getParameter("lang")).thenReturn("en");

        String result = bookController.deleteBook(bookName, request, redirectAttributes);

        assertEquals("redirect:/books?lang=en", result);
        verify(bookService).deleteBookByName(bookName);
        verify(redirectAttributes).addFlashAttribute("success", "Book deleted successfully!");
    }
}