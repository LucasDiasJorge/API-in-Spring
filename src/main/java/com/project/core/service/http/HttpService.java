package com.project.core.service.http;

import com.project.core.enums.EAuthType;
import com.project.core.service.http.model.HttpAuthTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {

    @Autowired
    protected RestTemplate restTemplate;

    public String baseUrl = "";

    protected ResponseEntity<Object> get(String url, HttpAuthTemplate auth) {
        String finalUrl = concatenateFinalUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "RFIDFacil/2.0 (Java 17; Linux)");

        if (auth.type != EAuthType.NONE) {
            headers.addAll(auth.asHeaders());
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Object> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.GET, httpEntity,
                Object.class);
        return responseEntity;
    }

    protected <T> ResponseEntity<T> get(String url, HttpAuthTemplate auth, Class<T> clazz) {
        String finalUrl = concatenateFinalUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "RFIDFacil/2.0 (Java 17; Linux)");

        if (auth.type != EAuthType.NONE) {
            headers.addAll(auth.asHeaders());
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.GET, httpEntity,
                clazz);
        return responseEntity;
    }

    protected <T> ResponseEntity<T> get(String url, HttpAuthTemplate auth, ParameterizedTypeReference<T> typeRef) {
        String finalUrl = concatenateFinalUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "RFIDFacil/2.0 (Java 17; Linux)");

        if (auth.type != EAuthType.NONE) {
            headers.addAll(auth.asHeaders());
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.GET, httpEntity, typeRef);
        return responseEntity;
    }

    protected ResponseEntity<Object> post(String url, Object request) {
        String finalUrl = concatenateFinalUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "RFIDFacil/2.0 (Java 17; Linux)");

        HttpEntity<Object> httpEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Object> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.POST, httpEntity,
                Object.class);

        return responseEntity;
    }

    protected ResponseEntity<Object> post(String url, Object request, HttpAuthTemplate auth) {
        String finalUrl = concatenateFinalUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "RFIDFacil/2.0 (Java 17; Linux)");

        if (auth.type != EAuthType.NONE) {
            headers.addAll(auth.asHeaders());
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Object> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.POST, httpEntity,
                Object.class);
        return responseEntity;
    }

    protected <T> ResponseEntity<T> post(String url, Object request, HttpAuthTemplate auth, Class<T> clazz) {
        String finalUrl = concatenateFinalUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "RFIDFacil/2.0 (Java 17; Linux)");

        if (auth.type != EAuthType.NONE) {
            headers.addAll(auth.asHeaders());
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(request, headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.POST, httpEntity,
                clazz);
        return responseEntity;
    }

    protected ResponseEntity<Object> put(String url, Object request, HttpAuthTemplate auth) {
        String finalUrl = concatenateFinalUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "RFIDFacil/2.0 (Java 17; Linux)");

        if (auth.type != EAuthType.NONE) {
            headers.addAll(auth.asHeaders());
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Object> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.PUT, httpEntity,
                Object.class);
        return responseEntity;
    }

    protected ResponseEntity<Object> delete(String url, Object request, HttpAuthTemplate auth) {
        String finalUrl = concatenateFinalUrl(url);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("User-Agent", "RFIDFacil/2.0 (Java 17; Linux)");

        if (auth.type != EAuthType.NONE) {
            headers.addAll(auth.asHeaders());
        }

        HttpEntity<Object> httpEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Object> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.DELETE, httpEntity,
                Object.class);
        return responseEntity;
    }

    protected String concatenateFinalUrl(String url) {
        return baseUrl + url;
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

}
