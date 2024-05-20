package com.project.core.advice.model;

import org.springframework.http.HttpStatusCode;

import java.util.UUID;

public class ResponseLoggingModel {

    private UUID id;
    private HttpStatusCode status;
    private Object body;

    public ResponseLoggingModel(UUID id, HttpStatusCode status, Object body) {
        this.id = id;
        this.status = status;
        this.body = body;
    }

    public static ResponseLoggingModel create(UUID id, HttpStatusCode status, Object body) {
        return new ResponseLoggingModel(id, status, body);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n[");
        sb.append("\n\tID: " + this.id.toString());
        sb.append("\n\tSTATUS: " + this.status.toString());
        sb.append("\n\tBODY: " + "\t" + this.body);
        sb.append("\n]");

        return sb.toString();
    }

    public UUID getId() {
        return id;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public Object getBody() {
        return body;
    }

}
