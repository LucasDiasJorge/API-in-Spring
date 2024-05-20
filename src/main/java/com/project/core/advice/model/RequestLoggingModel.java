package com.project.core.advice.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

public class RequestLoggingModel {

    private UUID id;
    private String url;
    private String method;
    private Map<String, String> params;
    private Map<String, String> pathVariables;
    private Object body;

    private RequestLoggingModel(UUID id, String url, String method, Map<String, String> params,
            Map<String, String> pathVariables, Object body) {
        this.id = id;
        this.url = url;
        this.params = params;
        this.pathVariables = pathVariables;
        this.body = body;
        this.method = method;
    }

    public static RequestLoggingModel create(UUID id, String url, String method, Map<String, String> params,
            Map<String, String> pathVariables, Object body) {
        return new RequestLoggingModel(id, url, method, params, pathVariables, body);
    }

    public String toString(ObjectMapper objectMapper) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append("\n[");
        sb.append("\n\tID: " + this.id.toString());
        sb.append("\n\tURL: " + this.url);
        sb.append("\n\tMETHOD: " + this.method);
        sb.append("\n\tPARAMS: " + this.params);
        sb.append("\n\tPATH VARIABLE: " + this.pathVariables);
        sb.append("\n\tBODY: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.body));
        sb.append("\n]");

        return sb.toString();
    }

    public UUID getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public Object getBody() {
        return body;
    }
}
