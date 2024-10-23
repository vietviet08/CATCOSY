package com.catcosy.admin.exeption;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(AccessDeniedException.class)
//        public String handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response) {
//        log.warn("into handleAccessDeniedException!");
//        return "redirect:/login";
//    }

}
