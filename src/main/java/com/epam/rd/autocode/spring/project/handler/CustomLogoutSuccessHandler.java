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
        
        Locale currentLocale = localeResolver.resolveLocale(request);
        String language = currentLocale.getLanguage();
        
        String redirectUrl = "/?logout&lang=" + language;
        response.sendRedirect(redirectUrl);
    }
}