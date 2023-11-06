package com.project.core.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteInfo {
    private String url;
    private String method;
    private String name;
    private List<String> requiredRoles;

    public RouteInfo(String url, String method,String name,List<String> requiredRoles) {
        this.url = url;
        this.method = method;
        this.name = name;
        this.requiredRoles = requiredRoles;
    }

    public RouteInfo() {
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(List<String> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }
    
}
