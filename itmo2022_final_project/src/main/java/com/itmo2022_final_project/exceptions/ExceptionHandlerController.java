package com.itmo2022_final_project.exceptions;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletException;

@Order(Ordered.HIGHEST_PRECEDENCE)

@Controller
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler({ServletException.class})
    public ErrorMessage handleServletException(ServletException ex) {
        return new ErrorMessage("Error while login ");
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ErrorMessage handleMissingParams(MissingServletRequestParameterException ex) {
        String parameter = ex.getParameterName();
        return new ErrorMessage(parameter + " header is missing");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ErrorMessage handleHeaderParams(MissingRequestHeaderException ex) {
        String name = ex.getHeaderName();
        return new ErrorMessage(name + " header is missing");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleClinicServiceException(AppException ex) {
        return new ErrorMessage(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleGlobalException(Exception ex) {
        return new ErrorMessage(ex.getMessage());
    }
}