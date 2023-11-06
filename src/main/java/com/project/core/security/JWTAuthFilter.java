package com.project.core.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.exception.response.ErrorResponse;
import com.project.core.model.administrative.UserModel;
import com.project.core.repository.UserRepository;
import com.project.core.security.auth.UserDetailsSeviceImpl;
import com.project.core.security.data.EAuthenticationMethod;
import com.project.core.security.data.UserDetailsData;

public class JWTAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    final UserRepository userRepository;
    final UserDetailsSeviceImpl userService;
    final Environment environment;
    private EAuthenticationMethod authenticationMethod;

    public JWTAuthFilter(AuthenticationManager authenticationManager, UserRepository userRepository,
            UserDetailsSeviceImpl userService, Environment environment) throws FileNotFoundException {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.environment = environment;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth", "POST"));
    }

    @Override
    @Transactional
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            UserModel attemptAuthUser = new ObjectMapper().readValue(request.getInputStream(), UserModel.class);
            UserModel userVerify = findUserAndAutenticationMethod(getAuthCredential(attemptAuthUser), attemptAuthUser);

            userVerify.setAttempts(userVerify.getAttempts() + 1);
            userVerify = userRepository.save(userVerify);

            try {
                Authentication auth = doAuthentication(attemptAuthUser, userVerify);
                userVerify.setAttempts(0);
                userVerify.setLastLogin(new Date());
                userRepository.save(userVerify);
                return auth;
            } catch (Exception e) {
                throw e;
            }
        } catch (BadCredentialsException e) {
            handleAuthenticationFailure(response, request, "Invalid email or password");
            return null;
        } catch (Exception e) {
            handleAuthenticationFailure(response, request, e.getMessage());
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        
        ObjectMapper objectMapper = new ObjectMapper();
        UserDetailsData userDetailsData = (UserDetailsData) authResult.getPrincipal();

        String token = generateJwtToken(userDetailsData);

        response.setContentType("application/json; charset=utf-8");

        Map<String, Object> ret = createAuthenticationResponse(token);

        response.getWriter().write(objectMapper.writeValueAsString(ret));

        System.out.println("Login from: " + request.getRemoteAddr());

        response.getWriter().flush();
    }

    private String generateJwtToken(UserDetailsData userDetailsData) {
        return JWT.create()
                .withSubject(userDetailsData.getUsername())
                .withClaim("roles",
                        userRepository.findByEmail(userDetailsData.getUsername()).getRoles().get(0).getRoleName()
                                .toString())
                .withExpiresAt(new Date(System.currentTimeMillis()
                        + Integer.parseInt(environment.getProperty("auth.token.expiration"))))
                .sign(Algorithm.HMAC512(environment.getProperty("auth.token.secret")));
    }

    private Map<String, Object> createAuthenticationResponse(String token) {
        Map<String, Object> ret = new HashMap<>();
        ret.put("data", Map.of("token", token));
        ret.put("status", 200);
        ret.put("message", "200 OK");
        return ret;
    }

    protected Authentication biometryAuth(UserModel attempAuthUser, UserModel userVerify) {
        attempAuthUser.setEmail(userVerify.getEmail());
        UserDetailsData userDetailsData = new UserDetailsData(attempAuthUser);
        return new UsernamePasswordAuthenticationToken(userDetailsData, null, userVerify.getAuthorities());
    }

    protected Authentication cardAuth(UserModel attempAuthUser, UserModel userVerify) {
        attempAuthUser.setEmail(userVerify.getEmail());
        UserDetailsData userDetailsData = new UserDetailsData(attempAuthUser);
        return new UsernamePasswordAuthenticationToken(userDetailsData, null, userVerify.getAuthorities());
    }

    protected Authentication emailPasswordAuth(UserModel attempAuthUser, UserModel userVerify) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                attempAuthUser.getEmail(), attempAuthUser.getPass(), userVerify.getRoles()));
    }

    protected UserModel findUserAndAutenticationMethod(String authCredential, UserModel attemptAuthUser) {
        UserModel authenticatedUser = null;

        if (attemptAuthUser.getEmail() != null) {
            authenticationMethod = EAuthenticationMethod.EMAIL_PASSWORD;
            authenticatedUser = userRepository.findByEmailOpt(authCredential)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else if (attemptAuthUser.getBiometry() != null) {
            authenticationMethod = EAuthenticationMethod.BIOMETRY;
            authenticatedUser = userRepository.findByBiometryAndDeletedByIsNull(authCredential)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else if (attemptAuthUser.getCard() != null) {
            authenticationMethod = EAuthenticationMethod.CARD;
            authenticatedUser = userRepository.findByCardAndDeletedByIsNull(authCredential)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } else {
            authenticationMethod = EAuthenticationMethod.INVALID;
        }

        return authenticatedUser;
    }

    protected String getAuthCredential(UserModel attemptAuthUser) {
        if (attemptAuthUser.getEmail() != null) {
            return attemptAuthUser.getEmail();
        }
        if (attemptAuthUser.getBiometry() != null) {
            return attemptAuthUser.getBiometry();
        }
        if (attemptAuthUser.getCard() != null) {
            return attemptAuthUser.getCard();
        }

        return null;
    }

    protected Authentication doAuthentication(UserModel attemptAuthUser, UserModel userVerify) throws Exception {
        switch (authenticationMethod) {
            case CARD:
                return cardAuth(attemptAuthUser, userVerify);
            case BIOMETRY:
                return biometryAuth(attemptAuthUser, userVerify);
            case EMAIL_PASSWORD:
                return emailPasswordAuth(attemptAuthUser, userVerify);
            case INVALID:
            default:
                throw new Exception("Invalid authentication method");
        }
    }

    private void handleAuthenticationFailure(HttpServletResponse response, HttpServletRequest request,
            String errorMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse responseModel = new ErrorResponse();
        responseModel.setCause(null);
        responseModel.setStatus(401);
        responseModel.setPath(request.getServletPath());
        responseModel.setTimestamp(Instant.now().toEpochMilli());
        responseModel.setMessage(errorMessage);
        responseModel.setSuggestion(null);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(401);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(responseModel));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
