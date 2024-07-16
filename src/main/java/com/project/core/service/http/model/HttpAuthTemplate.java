package com.project.core.service.http.model;

import com.project.core.enums.EAuthType;
import org.springframework.http.HttpHeaders;

import java.util.Base64;

public class HttpAuthTemplate {

    public String credential;
    public EAuthType type;

    public HttpAuthTemplate(String credential, EAuthType authType) {
        this.credential = credential;
        this.type = authType;
    }

    public static HttpAuthTemplate create(String credential, EAuthType authType) {
        return buildForType(new HttpAuthTemplate(credential, authType));
    }

    public HttpHeaders asHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", credential);
        return headers;
    }

    public HttpHeaders asHeaders(String headerKey){
        HttpHeaders headers = new HttpHeaders();
        headers.add(headerKey, credential);
        return headers;
    }

    public static String basicToBase64(String user, String password){
        String authString = user + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(authString.getBytes());
        return new String(encodedAuth);
    }

    private static HttpAuthTemplate buildForType(HttpAuthTemplate authTemplate) {
        switch (authTemplate.type) {
            case BASIC:
                return new HttpAuthTemplate("Basic " + authTemplate.credential, EAuthType.BASIC);
            case BEARER:
                return new HttpAuthTemplate("Bearer " + authTemplate.credential, EAuthType.BEARER);
            case API_KEY:
                return new HttpAuthTemplate(authTemplate.credential, EAuthType.API_KEY);
            case NONE:
                return new HttpAuthTemplate("", EAuthType.NONE);

        }
        return new HttpAuthTemplate("", EAuthType.NONE);
    }

}
