package com.project.core.exception.handler;

import java.io.IOException;
import java.time.Instant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.exception.response.ErrorResponse;

@Component
public class AppAccessDeniedHandler implements AccessDeniedHandler{

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorResponse responseModel = new ErrorResponse();
        responseModel.setCause(null);
        responseModel.setStatus(401);
        responseModel.setPath(request.getServletPath());
        responseModel.setTimestamp(Instant.now().toEpochMilli());
        responseModel.setMessage("Failed to validate JWT");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(401);
        response.getWriter().write(objectMapper.writeValueAsString(responseModel));// TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }

    
    
}
