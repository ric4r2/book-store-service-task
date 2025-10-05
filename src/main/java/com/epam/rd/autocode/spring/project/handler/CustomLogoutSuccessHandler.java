package com.epam.rd.autocode.spring.project.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.Locale;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final LocaleResolver localeResolver;

    public CustomLogoutSuccessHandler(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, 
                              Authentication authentication) throws IOException, ServletException {
        
        // Get the current locale from the session
        Locale currentLocale = localeResolver.resolveLocale(request);
        String language = currentLocale.getLanguage();
        
        // Redirect to home page with logout parameter and language preserved
        String redirectUrl = "/?logout&lang=" + language;
        response.sendRedirect(redirectUrl);
    }
}