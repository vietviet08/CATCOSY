package com.dacs1.admin.exeption;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleForbiddenException(HttpServletRequest request,
                                                 Exception ex) {
        ModelAndView modelAndView = new ModelAndView("login");
        return modelAndView;
    }

}
