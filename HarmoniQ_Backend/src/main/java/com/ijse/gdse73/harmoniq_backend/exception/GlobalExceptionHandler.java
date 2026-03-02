package com.ijse.gdse73.harmoniq_backend.exception;

import com.ijse.gdse73.harmoniq_backend.dto.APIResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public APIResponse handleUserNameNotFound(UsernameNotFoundException e){
        return new APIResponse(
                HttpStatus.NOT_FOUND.value(),
                "Username not found",
                e.getMessage()
        );
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public APIResponse handleBadCredentialsException(BadCredentialsException e){
        return new APIResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Username or Password is incorrect",
                e.getMessage()
        );
    }

    @ExceptionHandler(value = {ExpiredJwtException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public APIResponse handleExpiredJwtException(ExpiredJwtException e){
        return new APIResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Expired Token",
                e.getMessage()
        );
    }

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public APIResponse handleRuntimeException(RuntimeException e){
        return new APIResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Occurred",
                e.getMessage()
        );
    }
}
