package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testUpdateEmployeeProfile() {
        String email = "employee@test.com";
        EmployeeDTO currentEmployee = new EmployeeDTO();
        currentEmployee.setPassword("oldPassword");
        
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("name")).thenReturn("Updated Name");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("birthDate")).thenReturn("1990-01-01");
        when(request.getParameter("password")).thenReturn("");
        when(request.getParameter("lang")).thenReturn("en");
        when(employeeService.getEmployeeByEmail(email)).thenReturn(currentEmployee);

        String result = employeeController.updateEmployeeProfile(request, authentication, redirectAttributes);

        assertEquals("redirect:/profile?lang=en", result);
        verify(employeeService).updateEmployeeByEmail(eq(email), any(EmployeeDTO.class));
        verify(redirectAttributes).addFlashAttribute("success", "Employee profile updated successfully!");
    }

    @Test
    void testUpdateEmployeeProfileWithNewPassword() {
        String email = "employee@test.com";
        
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("name")).thenReturn("Updated Name");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("birthDate")).thenReturn("1990-01-01");
        when(request.getParameter("password")).thenReturn("newPassword");
        when(request.getParameter("lang")).thenReturn("en");

        String result = employeeController.updateEmployeeProfile(request, authentication, redirectAttributes);

        assertEquals("redirect:/profile?lang=en", result);
        verify(employeeService).updateEmployeeByEmail(eq(email), any(EmployeeDTO.class));
        verify(redirectAttributes).addFlashAttribute("success", "Employee profile updated successfully!");
    }

    @Test
    void testUpdateEmployeeProfileWithEmptyBirthDate() {
        String email = "employee@test.com";
        EmployeeDTO currentEmployee = new EmployeeDTO();
        currentEmployee.setPassword("oldPassword");
        
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("name")).thenReturn("Updated Name");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("birthDate")).thenReturn("");
        when(request.getParameter("password")).thenReturn("");
        when(request.getParameter("lang")).thenReturn("en");
        when(employeeService.getEmployeeByEmail(email)).thenReturn(currentEmployee);

        String result = employeeController.updateEmployeeProfile(request, authentication, redirectAttributes);

        assertEquals("redirect:/profile?lang=en", result);
        verify(employeeService).updateEmployeeByEmail(eq(email), any(EmployeeDTO.class));
        verify(redirectAttributes).addFlashAttribute("success", "Employee profile updated successfully!");
    }

    @Test
    void testUpdateEmployeeProfileWithException() {
        String email = "employee@test.com";
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("name")).thenReturn("Updated Name");
        when(request.getParameter("phone")).thenReturn("1234567890");
        when(request.getParameter("birthDate")).thenReturn("1990-01-01");
        when(request.getParameter("password")).thenReturn("");
        when(request.getParameter("lang")).thenReturn("en");
        when(employeeService.getEmployeeByEmail(email)).thenThrow(new RuntimeException("Error"));

        String result = employeeController.updateEmployeeProfile(request, authentication, redirectAttributes);

        assertEquals("redirect:/profile?lang=en", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Failed to update profile"));
    }

    @Test
    void testDeleteEmployeeProfile() {
        String email = "employee@test.com";
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("lang")).thenReturn("en");

        String result = employeeController.deleteEmployeeProfile(authentication, redirectAttributes, request);

        assertEquals("redirect:/logout?lang=en", result);
        verify(employeeService).deleteEmployeeByEmail(email);
        verify(redirectAttributes).addFlashAttribute("success", "Account deleted successfully!");
    }

    @Test
    void testDeleteEmployeeProfileWithException() {
        String email = "employee@test.com";
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("lang")).thenReturn("en");
        doThrow(new RuntimeException("Error")).when(employeeService).deleteEmployeeByEmail(email);

        String result = employeeController.deleteEmployeeProfile(authentication, redirectAttributes, request);

        assertEquals("redirect:/profile", result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Failed to delete account"));
    }

    @Test
    void testDeleteEmployeeProfileWithNullLang() {
        String email = "employee@test.com";
        when(authentication.getName()).thenReturn(email);
        when(request.getParameter("lang")).thenReturn(null);

        String result = employeeController.deleteEmployeeProfile(authentication, redirectAttributes, request);

        assertEquals("redirect:/logout?lang=en", result);
        verify(employeeService).deleteEmployeeByEmail(email);
        verify(redirectAttributes).addFlashAttribute("success", "Account deleted successfully!");
    }
}