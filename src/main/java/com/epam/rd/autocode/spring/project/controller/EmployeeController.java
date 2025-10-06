package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    
    @PostMapping("/profile/update")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String updateEmployeeProfile(HttpServletRequest request,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        try {
            String userEmail = authentication.getName();
            
            EmployeeDTO employeeDto = new EmployeeDTO();
            employeeDto.setEmail(userEmail);
            employeeDto.setName(request.getParameter("name"));
            employeeDto.setPhone(request.getParameter("phone"));
            
            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
                employeeDto.setBirthDate(LocalDate.parse(birthDateStr));
            }
            
            String password = request.getParameter("password");
            if (password == null || password.trim().isEmpty()) {
                EmployeeDTO currentEmployee = employeeService.getEmployeeByEmail(userEmail);
                employeeDto.setPassword(currentEmployee.getPassword());
            } else {
                employeeDto.setPassword(password);
            }
            
            employeeService.updateEmployeeByEmail(userEmail, employeeDto);
            redirectAttributes.addFlashAttribute("success", "Employee profile updated successfully!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        
        String lang = request.getParameter("lang");
        return "redirect:/profile" + (lang != null ? "?lang=" + lang : "");
    }
    
    @PostMapping("/profile/delete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String deleteEmployeeProfile(Authentication authentication, 
                                      RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {
        try {
            String userEmail = authentication.getName();
            
            String lang = request.getParameter("lang");
            if (lang == null) {
                lang = "en";
            }
            
            employeeService.deleteEmployeeByEmail(userEmail);
            redirectAttributes.addFlashAttribute("success", "Account deleted successfully!");
            return "redirect:/logout?lang=" + lang;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete account: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}