package com.dacs1.customer.interceptor;

import com.dacs1.library.model.Cart;
import com.dacs1.library.model.Customer;
import com.dacs1.library.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Component
public class CommonInterceptor implements HandlerInterceptor {

    private final CustomerService customerService;

    @Autowired
    public CommonInterceptor(CustomerService customerService) {
        this.customerService = customerService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {


        HttpSession session = request.getSession();
        if (session != null) {
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                Customer customer = customerService.findByUsername(principal.getName());
                session.setAttribute("nameForCustomer", customer.getFirstName() + " " + customer.getLastName());
                session.setAttribute("logged", true);

                Cart cart = customer.getCart();
                if (cart == null) session.setAttribute("totalProduct", 0);
                else session.setAttribute("totalProduct", cart.getTotalItem());
            } else {
                session.setAttribute("logged", false);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
    }
}
