package com.project.core.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private Integer status;
    private String message;
    private String path;
    private String cause;
    private String suggestion;
    private Long timestamp;
    private Map<String, List<?>> util;



    public ErrorResponse(Integer status, String message, String path, String cause, String suggestion, Long timestamp) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.cause = cause;
        this.suggestion = suggestion;
        this.timestamp = timestamp;
    }

    public ErrorResponse(Integer status, String message, String path, Long timestamp) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    public ErrorResponse() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, List<?>> getUtil() {
        return util;
    }

    public void setUtil(Map<String, List<?>> util) {
        this.util = util;
    }
}
