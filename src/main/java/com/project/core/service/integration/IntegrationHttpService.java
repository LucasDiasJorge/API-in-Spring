package com.project.core.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.core.enums.EAuthType;
import com.project.core.model.integration.IntegrationModel;
import com.project.core.service.http.HttpService;
import com.project.core.service.http.model.HttpAuthTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IntegrationHttpService extends HttpService {

    public IntegrationHttpService() {
    }

    public String authBasic(String url, String credential) {
        Map response = (Map) post(url, null, HttpAuthTemplate.create(credential, EAuthType.BASIC)).getBody();
        String token = (String) response.get("token");
        return token;
    }

    public String authMap(String url, String body) {
        Map response = (Map) post(url, body).getBody();
        return (String) response.get("token");
    }

    public String getAuthToken(IntegrationModel integrationAction, String user, String pass, String body) throws JsonProcessingException {
        String token = null;

        switch (integrationAction.getAuthRouteType()) {
            case BEARER:
                token = authMap(
                        integrationAction.getUriAuth(),
                        body
                );
                break;
            case BASIC:
                token = authBasic(
                        integrationAction.getUriAuth(),
                        HttpAuthTemplate.basicToBase64(user, pass)
                );
                break;
            default:
                throw new IllegalArgumentException("Tipo de autenticação desconhecido: " + integrationAction.getAuthType());
        }

        return token;
    }

    public HttpStatusCode performMirrorAction(IntegrationModel integrationModel, Map<String,Object> body, String token){

        Map<String, List<Map<String,Object>>> bodyTreated = new HashMap<>();

        List<Map<String,Object>> listTreated = new ArrayList<>();

        listTreated.add(body);

        bodyTreated.put("rfid",listTreated);

        System.out.println(bodyTreated);

        ResponseEntity<Object> responseEntity = post(integrationModel.getUri(), bodyTreated, HttpAuthTemplate.create(token,EAuthType.BEARER));

        return responseEntity.getStatusCode();
    }

}
