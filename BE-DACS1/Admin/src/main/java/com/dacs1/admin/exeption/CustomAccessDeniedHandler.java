package com.dacs1.admin.exeption;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        // Log the exception
        System.out.println("Access Denied: " + accessDeniedException.getMessage());

        if (request.getUserPrincipal() != null){
            response.sendRedirect(request.getContextPath() + "/403");
            return;
        }
        // Redirect to the login page
        response.sendRedirect(request.getContextPath() + "/login");
    }
}