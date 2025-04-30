package com.catcosy.customer.config.handler;

import com.catcosy.library.model.Customer;
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
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Autowired
    private CustomerService customerService;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String message = "";

        Customer customer = customerService.findByUsername(request.getParameter("username"));
        if(customer == null) {
            message = "Password or username not correct!";
            response.sendRedirect(request.getContextPath() + "/login?error");
            return;
        }
        else if(!customer.isActive()){
            message = "Your account is locked, please contact with admin to know another detail!";
            response.sendRedirect(request.getContextPath() + "/login?locked");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}
