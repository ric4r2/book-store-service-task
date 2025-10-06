package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;
    
    // HTML Form Handling Methods for Book Management
    
    @PostMapping("/add")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String addBook(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            BookDTO bookDto = new BookDTO();
            bookDto.setName(request.getParameter("name"));
            bookDto.setGenre(request.getParameter("genre"));
            bookDto.setAgeGroup(AgeGroup.valueOf(request.getParameter("ageGroup")));
            bookDto.setPrice(new BigDecimal(request.getParameter("price")));
            bookDto.setAuthor(request.getParameter("author"));
            bookDto.setPages(Integer.parseInt(request.getParameter("pages")));
            bookDto.setCharacteristics(request.getParameter("characteristics"));
            bookDto.setDescription(request.getParameter("description"));
            bookDto.setLanguage(Language.valueOf(request.getParameter("language")));
            
            // Parse publication date
            String publicationDateStr = request.getParameter("publicationDate");
            if (publicationDateStr != null && !publicationDateStr.trim().isEmpty()) {
                bookDto.setPublicationDate(LocalDate.parse(publicationDateStr));
            }
            
            bookService.addBook(bookDto);
            redirectAttributes.addFlashAttribute("success", "Book added successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add book: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/books" + (lang != null ? "?lang=" + lang : "");
    }
    
    @PostMapping("/edit/{name}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String editBook(@PathVariable String name, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            BookDTO bookDto = new BookDTO();
            bookDto.setName(request.getParameter("name"));
            bookDto.setGenre(request.getParameter("genre"));
            bookDto.setAgeGroup(AgeGroup.valueOf(request.getParameter("ageGroup")));
            bookDto.setPrice(new BigDecimal(request.getParameter("price")));
            bookDto.setAuthor(request.getParameter("author"));
            bookDto.setPages(Integer.parseInt(request.getParameter("pages")));
            bookDto.setCharacteristics(request.getParameter("characteristics"));
            bookDto.setDescription(request.getParameter("description"));
            bookDto.setLanguage(Language.valueOf(request.getParameter("language")));
            
            // Parse publication date
            String publicationDateStr = request.getParameter("publicationDate");
            if (publicationDateStr != null && !publicationDateStr.trim().isEmpty()) {
                bookDto.setPublicationDate(LocalDate.parse(publicationDateStr));
            }
            
            bookService.updateBookByName(name, bookDto);
            redirectAttributes.addFlashAttribute("success", "Book updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update book: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/books" + (lang != null ? "?lang=" + lang : "");
    }
    
    @PostMapping("/delete/{name}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String deleteBook(@PathVariable String name, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBookByName(name);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete book: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/books" + (lang != null ? "?lang=" + lang : "");
    }
}
