package com.project.core.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.exception.response.ErrorResponse;
import com.project.core.exception.throwable.AppException;
import com.project.core.repository.UserRepository;

public class JWTFilterValidator extends BasicAuthenticationFilter {

    public static final String HEADER_ATRIBUTE = "Authorization";

    public static final String ATRIBUTE_PREFIX = "Bearer ";

    final UserRepository userRepository;

    final Environment environment;

    public JWTFilterValidator(AuthenticationManager authenticationManager, UserRepository userRepository,
            Environment environment) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        String atribute = request.getHeader(HEADER_ATRIBUTE);

        System.out.println(request.getRequestURL());

        if (atribute == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!atribute.startsWith(ATRIBUTE_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String token = atribute.replace(ATRIBUTE_PREFIX, "");

        UsernamePasswordAuthenticationToken authenticationToken;
        try {
            authenticationToken = getAuthenticatorToken(token);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } catch (AppException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            ErrorResponse responseModel = new ErrorResponse();
            responseModel.setCause(null);
            responseModel.setStatus(403);
            responseModel.setPath(request.getServletPath());
            responseModel.setTimestamp(Instant.now().toEpochMilli());
            responseModel.setMessage(e.getMessage());
            responseModel.setSuggestion(null);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(403);
            response.getWriter().write(objectMapper.writeValueAsString(responseModel));
            e.printStackTrace();
        }
    }

    private UsernamePasswordAuthenticationToken getAuthenticatorToken(String token) throws AppException {
        try {
            String user = JWT.require(Algorithm.HMAC512(environment.getProperty("auth.token.secret")))
                    .build()
                    .verify(token)
                    .getSubject();

            if (user == null) {
                return null;
            }

            Map<String, Claim> roles = JWT.require(Algorithm.HMAC512(environment.getProperty("auth.token.secret")))
                    .build()
                    .verify(token)
                    .getClaims();

            // UserModel userModel = userRepository.findByEmail(user);

            String role = (roles.get("roles").asString());

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            switch (role) {
                case "ROLE_ROOT" -> authorities.add(new SimpleGrantedAuthority("ROLE_ROOT"));
                case "ROLE_ADMIN" -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                case "ROLE_USER" -> authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            return new UsernamePasswordAuthenticationToken(user, null, authorities); // ArrayList = permissionList
        } catch (Exception e) {
            throw new AppException(e.getMessage(), 403, e.getLocalizedMessage());
        }

    }

}
