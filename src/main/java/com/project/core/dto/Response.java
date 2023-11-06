package com.project.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private T data;
    private String user;
    private int status;
    private String message;
    private Map<String, List<String>> utils;

    public Response(T data, String user, int status, String message) {
        this.data = data;
        this.user = user;
        this.status = status;
        this.message = message;
    }

    public Response(int status, String message){
        this.status = status;
        this.message = message;
    }

    public Response(T data, String user, int status, String message, Map<String,List<String>> utils) {
        this.data = data;
        this.user = user;
        this.status = status;
        this.message = message;
        this.utils = utils;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, List<String>> getUtils() {
        return utils;
    }

    public void setUtils(Map<String, List<String>> utils) {
        this.utils = utils;
    }
    
}
