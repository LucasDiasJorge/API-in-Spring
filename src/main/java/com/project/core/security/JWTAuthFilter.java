package com.project.core.security;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.RoleModel;
import com.project.core.security.auth.UserDetailsSeviceImpl;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.exception.response.ErrorResponse;
import com.project.core.model.administrative.UserModel;
import com.project.core.repository.UserRepository;

public class JWTAuthFilter extends BasicAuthenticationFilter {

    public static final String HEADER_ATRIBUTE = "Authorization";

    public static final String ATRIBUTE_PREFIX = "Bearer ";

    final UserRepository userRepository;

    final Environment environment;

    public JWTAuthFilter(AuthenticationManager authenticationManager, UserRepository userRepository,
                         UserDetailsSeviceImpl userService, Environment environment) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.environment = environment;
    }

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
            logger.error(e.getMessage(), null);
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

            UserModel userModel = userRepository.findByEmail(user);

            if(userModel == null){
                throw new AppException("User not found",403,null);
            }

            if(!userModel.isActive()){
                throw new AppException("User is not Active",403,null);
            }

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            for (RoleModel role : userModel.getRoles().stream().sorted(new Comparator<RoleModel>() {

                @Override
                public int compare(RoleModel role0, RoleModel role1) {
                    if(role0.getPriority() < role1.getPriority()){
                        return -1;
                    }else if(role0.getPriority() > role1.getPriority()){
                        return 1;
                    }else{
                        return 0;
                    }
                }

            }).toList()) {
                authorities.add(new SimpleGrantedAuthority(role.getRoleName().toString()));
            }
            return new UsernamePasswordAuthenticationToken(user, null, authorities); // ArrayList = permissionList
        } catch (Exception e) {
            throw new AppException(e.getMessage(), 403, e.getLocalizedMessage());
        }

    }

}
