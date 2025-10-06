package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByName(String name);
    void deleteByName(String name);
    boolean existsByName(String name);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Book> findBooksWithSearch(@Param("search") String search, Pageable pageable);
}
