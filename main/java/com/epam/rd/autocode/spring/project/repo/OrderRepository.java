package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.client.email = :email")
    List<Order> findAllByClientEmail(@Param("email") String email);
    
    @Query("SELECT o FROM Order o WHERE o.employee.email = :email")
    List<Order> findAllByEmployeeEmail(@Param("email") String email);
}
