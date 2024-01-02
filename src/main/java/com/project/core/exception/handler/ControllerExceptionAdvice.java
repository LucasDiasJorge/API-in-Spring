package com.project.core.exception.handler;

import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.hibernate.TransactionException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonParseException;
import com.project.core.exception.response.ErrorResponse;
import com.project.core.exception.throwable.AppException;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appExceptionHandler(AppException ex, HttpServletRequest request,
            HttpServletResponse response) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getServletPath());

        if(ex.getCause() != null){
            Throwable rootCause = getRootCause(ex);
            errorResponse.setCause(rootCause.getMessage());
        }

        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatus(ex.getStatus());
        errorResponse.setSuggestion(ex.getSuggestion());
        errorResponse.setTimestamp(Instant.now().toEpochMilli());
        errorResponse.setUtil(ex.getUtil());

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<?> exceptionHandler(Exception ex, HttpServletRequest request, HttpServletResponse response){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getServletPath());

        if(ex.getCause() != null){
            Throwable rootCause = getRootCause(ex);
            errorResponse.setCause(rootCause.getMessage());
        }

        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatus(500);
        errorResponse.setSuggestion(null);
        errorResponse.setTimestamp(Instant.now().toEpochMilli());
        errorResponse.setUtil(null);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> httpMessageNotReadableExceptionHandler(MethodArgumentNotValidException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getServletPath());

        if(ex.getCause() != null){
            Throwable rootCause = getRootCause(ex);
            errorResponse.setCause(rootCause.getMessage());
        }

        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        errorResponse.setMessage(errorMessage);        
        errorResponse.setStatus(400);
        errorResponse.setSuggestion(null);
        errorResponse.setTimestamp(Instant.now().toEpochMilli());
        errorResponse.setUtil(null);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getServletPath());

        if(ex.getCause() != null){
            Throwable rootCause = getRootCause(ex);
            errorResponse.setCause(rootCause.getMessage());
        }

        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        errorResponse.setMessage(errorMessage);        
        errorResponse.setStatus(400);
        errorResponse.setSuggestion(null);
        errorResponse.setTimestamp(Instant.now().toEpochMilli());
        errorResponse.setUtil(null);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex, HttpServletRequest request,
            HttpServletResponse response) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getServletPath());

        if(ex.getCause() != null){
            errorResponse.setCause(ex.getMostSpecificCause().getMessage());
        }
        errorResponse.setMessage("Erro while validating route param '" +  ex.getParameter().getParameterName() + "' with value [" + ex.getValue() + "]");
        errorResponse.setStatus(400);
        errorResponse.setSuggestion("Required type is " + ex.getRequiredType());
        errorResponse.setTimestamp(Instant.now().toEpochMilli());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }



    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> tokenExpiredExceptionHandler(TokenExpiredException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getServletPath());

        if(ex.getCause() != null){
            Throwable rootCause = getRootCause(ex);
            errorResponse.setCause(rootCause.getMessage());
        }

        String errorMessage = ex.getMessage();
        errorResponse.setMessage(errorMessage);
        errorResponse.setStatus(403);
        errorResponse.setSuggestion(null);
        errorResponse.setTimestamp(Instant.now().toEpochMilli());
        errorResponse.setUtil(null);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> userNameNotFoundExceptionHandler(UsernameNotFoundException ex, HttpServletRequest request)
    {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getServletPath());

        if(ex.getCause() != null){
            Throwable rootCause = getRootCause(ex);
            errorResponse.setCause(rootCause.getMessage());
        }

        String errorMessage = ex.getMessage();
        errorResponse.setMessage(errorMessage);        
        errorResponse.setStatus(400);
        errorResponse.setSuggestion("Verify email and try again");
        errorResponse.setTimestamp(Instant.now().toEpochMilli());
        errorResponse.setUtil(null);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    // Helper method to get the root cause of an exception
    private Throwable getRootCause(Throwable ex) {
        Throwable rootCause = ex;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<?> timeoutExceptionHandler(TransactionException ex, HttpServletRequest request){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getServletPath());

        String errorMessage = ex.getMessage();
        errorResponse.setMessage(errorMessage);
        errorResponse.setStatus(408);
        errorResponse.setSuggestion("Try again later");
        errorResponse.setTimestamp(Instant.now().toEpochMilli());
        errorResponse.setUtil(null);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

}
