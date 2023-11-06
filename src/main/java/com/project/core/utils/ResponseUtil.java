package com.project.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.dto.Response;

public class ResponseUtil {

    public static <T> ResponseEntity<Response<T>> createResponse(T data, int status, String message, Principal user){
        Response<T> response = new Response<T>(data, user.getName(), status, message);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<Response<T>> createResponseWithContentLength(T data, int status, String message, Principal user) throws IOException {
        Response<T> response = new Response<T>(data, user.getName(), status, message);

        HttpHeaders httpHeaders = new HttpHeaders();
        ObjectMapper objMapper = new ObjectMapper();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        JsonGenerator jsonGenerator = objMapper.getFactory().createGenerator(byteArrayOutputStream);
        objMapper.writeValue(jsonGenerator, response);
        httpHeaders.add(HttpHeaders.CONTENT_LENGTH,String.valueOf(byteArrayOutputStream.size()));        
        ResponseEntity<Response<T>> responseEntity = ResponseEntity.status(status).headers(httpHeaders).body(response);
        return responseEntity;
    }

    public static <T> ResponseEntity<Response<List<T>>> createResponse(List<T> data, int status, String message,
            Principal user) {
        Response<List<T>> response = new Response<List<T>>(data, user.getName(), status, message);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<Response<T>> createResponse(T data, int status, String message) {
        Response<T> response = new Response<T>(data, null, status, message);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<Response<T>> createTimeoutResponse(T data, int status, String message){
        Response<T> response = new Response<T>(status,message);
        return ResponseEntity.status(status).body(response);
    }
}
