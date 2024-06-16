package com.dacs1.customer.config.oauth2;

import com.dacs1.library.service.CustomerService;
import jakarta.persistence.Column;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOauth2User user = (CustomOauth2User) authentication.getPrincipal();
        customerService.processOAuthLogin(user.getName(), user.getEmail());

        response.sendRedirect("/shop");
    }
}
