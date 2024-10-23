package com.catcosy.library.service.oauth2.security.handler;

import com.catcosy.library.service.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

//        String message = "";
//
//        CustomerDto customer = customerService.findByEmail(request.getParameter("email"));
//        if(customer != null) {
//            message = "Password or username not correct!";
//            response.sendRedirect(request.getContextPath() + "/login?error");
//            return;
//        }

        response.sendRedirect("/login");
    }
}
