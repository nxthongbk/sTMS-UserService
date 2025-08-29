package com.scity.user.exception;

import com.scity.user.model.dto.ResModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResModel<String> handleAllException(Exception ex, WebRequest webRequest) {
        return new ResModel<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResModel<String> handlerRequestException(NotFoundException ex) {
        return new ResModel<>(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler({
            BadRequestException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResModel<String> handlerRequestException(BadRequestException ex) {
        return new ResModel<>(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({
            AuthenticationException.class,
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResModel<String> unauthorizedException(AuthenticationException ex) {
        return new ResModel<>(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }

    @ExceptionHandler({ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResModel<String> forbiddenException(ForbiddenException ex) {
        return new ResModel<>(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResModel<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        if (ex.getBindingResult().getAllErrors().isEmpty()) {
            return new ResModel<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        }
        ObjectError error = ex.getBindingResult().getAllErrors().get(0);
        String[] elements = Objects.requireNonNull(error.getDefaultMessage()).split("\\|");
        if (elements.length > 1)
            return new ResModel<>(Integer.parseInt(elements[1]), elements[0]);
        return new ResModel<>(HttpStatus.NO_CONTENT.value(), elements[0]);
    }
}