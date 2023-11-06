package com.project.core.exception.handler;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.exception.response.ErrorResponse;

@Component
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint{

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorResponse responseModel = new ErrorResponse();
        responseModel.setCause(null);
        responseModel.setStatus(401);
        responseModel.setPath(request.getServletPath());
        responseModel.setTimestamp(Instant.now().toEpochMilli());
        responseModel.setMessage("Failed to validate JWT");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(401);
        response.getWriter().write(objectMapper.writeValueAsString(responseModel));
    }
    
}
