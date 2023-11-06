package com.project.core.exception.throwable;

import java.util.List;
import java.util.Map;

public class AppException extends Exception {

    private Integer status;
    private String suggestion;
    private Map<String,List<?>> util;

    public AppException(Integer status, String suggestion) {
        this.status = status;
        this.suggestion = suggestion;
    }

    public AppException(Integer status, String suggestion, Map<String, List<?>> util) {
        this.status = status;
        this.suggestion = suggestion;
        this.util = util;
    }

    public AppException(String message, Integer status, String suggestion) {
        super(message);
        this.status = status;
        this.suggestion = suggestion;
    }

    public AppException(Throwable cause, Integer status, String suggestion) {
        super(cause);
        this.status = status;
        this.suggestion = suggestion;
    }

    public AppException(String message, Throwable cause, Integer status, String suggestion) {
        super(message, cause);
        this.status = status;
        this.suggestion = suggestion;
    }

    public AppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
            Integer status, String suggestion) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
        this.suggestion = suggestion;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Map<String, List<?>> getUtil() {
        return util;
    }

    public void setUtil(Map<String, List<?>> util) {
        this.util = util;
    }
}
