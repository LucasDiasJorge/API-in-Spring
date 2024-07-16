package com.project.core.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.enums.EAuthType;
import com.project.core.model.administrative.UserModel;
import com.project.core.model.integration.IntegrationModel;
import com.project.core.model.integration.MirrorModel;
import com.project.core.repository.IntegrationModelRepository;
import com.project.core.repository.UserRepository;
import com.project.core.utils.StringUtil;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Service
public class IntegrationInterceptorService {

    final private UserRepository userRepository;
    final private IntegrationModelRepository integrationModelRepository;
    final private IntegrationHttpService http;
    final Environment environment;

    public IntegrationInterceptorService(UserRepository userRepository, IntegrationModelRepository integrationModelRepository, IntegrationHttpService http, Environment environment) {
        this.userRepository = userRepository;
        this.integrationModelRepository = integrationModelRepository;
        this.http = http;
        this.environment = environment;
    }

    private String buildAuthMap(MirrorModel mirrorModel, String userPropValue, String passPropValue) {

        String authMapper = mirrorModel.getAuthConvert();

        if (authMapper == null || authMapper.isEmpty()) {
            throw new IllegalArgumentException("authMapper string is null or empty");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> authMap;

        try {
            authMap = objectMapper.readValue(authMapper, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON string to Map", e);
        }

        authMap.put("user", userPropValue);
        authMap.put("pass", passPropValue);

        try {
            return objectMapper.writeValueAsString(authMap);
        } catch (IOException e) {
            throw new RuntimeException("Error converting Map to JSON string", e);
        }
    }

    private void performMirrorAction(IntegrationModel integrationAction, Principal principal, String token) throws JsonProcessingException {

        // Switch action

    }

    public void integrationMirror(HttpServletRequest request, HttpServletResponse response, Object handler){

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName();
            System.out.println("User: " + username);
        } else {
            System.out.println("No authenticated user");
            return;
        }

        if (!Objects.equals(request.getMethod(), "POST") && !Objects.equals(request.getMethod(), "PUT")) {
            System.out.println("Not a POST or PUT request");
            return;
        }


        UserModel user = userRepository.findByEmail(principal.getName());

        if (user.getCompany().getIntegration()) {

            IntegrationModel integrationAction = integrationModelRepository.findByTriggers(
                    user.getCompany().getId(),
                    StringUtil.normalizeUri(request.getRequestURI()),
                    request.getMethod()
            );

            String clientKey = user.getCompany().getClientKey();

            String userPropKey = "user." + clientKey;
            String userPropValue = environment.getProperty(userPropKey);

            String passPropKey = "pass." + clientKey;
            String passPropValue = environment.getProperty(passPropKey);

            String token = null;

            if(integrationAction.getAuthRouteType() == EAuthType.BASIC){
                try {
                    token = http.getAuthToken(integrationAction, userPropValue, passPropValue, null);
                } catch (JsonProcessingException e) {
                    System.out.println(e);
                }
            }
            if(integrationAction.getAuthRouteType() == EAuthType.BEARER){
                try {
                    token = http.getAuthToken(integrationAction, null, null, buildAuthMap(integrationAction.getMirrorAuth(),userPropValue,passPropValue));
                } catch (JsonProcessingException e) {
                    System.out.println(e);
                }
            }

            try {
                performMirrorAction(integrationAction, principal, token);
            } catch (JsonProcessingException e) {
                System.out.println(e);
            }

        }
    }
}
