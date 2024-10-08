package com.catcosy.admin.interceptor;

import com.catcosy.library.model.Admin;
import com.catcosy.library.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.stream.Collectors;

@Component
public class CommonInterceptor implements HandlerInterceptor {

    private AdminService adminService;

    @Autowired
    public CommonInterceptor(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {


        HttpSession session = request.getSession();
        if (session != null) {
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                Admin admin = adminService.findByUsername(principal.getName());
                if (admin != null) {
                    session.setAttribute("nameForAdmin", admin.getFirstName() + " " + admin.getLastName());
                    session.setAttribute("logged", true);
                    session.setAttribute("roleFor", admin.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()) );
                    session.setAttribute("idAd", admin.getId() );
                    session.setAttribute("avatarImage", admin.getImage());

                }
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
