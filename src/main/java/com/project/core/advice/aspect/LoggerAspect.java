package com.project.core.advice.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.advice.model.RequestLoggingModel;
import com.project.core.advice.model.ResponseLoggingModel;
import com.project.core.dto.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class LoggerAspect {

    private final Logger logger = LogManager.getLogger("CONTROLLER");

    @Autowired
    private ObjectMapper objectMapper;

    private UUID requestId;

    @Before("execution(* com.project.core.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestId = UUID.randomUUID();

            String url = request.getRequestURL().toString();
            String httpMethod = request.getMethod();
            Map<String, String[]> params = request.getParameterMap();
            Map<String, String> paramsMap = convertParamsToStringMap(params);
            Map<String, String> pathVariables = getPathVariables(request);

            String requestBody = getRequestBody(request);

            RequestLoggingModel requestModel = RequestLoggingModel.create(requestId, url,
                    httpMethod, paramsMap, pathVariables,
                    (requestBody != null && !requestBody.isBlank()) ? requestBody : "<empty>");
            logRequest(requestModel);
        }
    }

    @AfterReturning(pointcut = "execution(* com.project.core.controller.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) throws Throwable {
        if (requestId != null) {
            if (result.getClass().isAssignableFrom(ResponseEntity.class)) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                Object body = responseEntity.getBody();

                if (body instanceof Response && ((Response<?>) body).getData() instanceof List) {
                    Response<?> response = ((Response<?>) body);
                    List<?> bodyList = ((List<?>) response.getData()).subList(0, 1);
                    HttpStatusCode statusBody = responseEntity.getStatusCode();
                    String responseBody = objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(bodyList);

                    ResponseLoggingModel responseModel = ResponseLoggingModel.create(requestId, statusBody,
                            responseBody);
                    logResponse(responseModel);
                    return;
                }

                HttpStatusCode statusBody = responseEntity.getStatusCode();
                String responseBody = objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(responseEntity.getBody());

                ResponseLoggingModel responseModel = ResponseLoggingModel.create(requestId, statusBody, responseBody);
                logResponse(responseModel);

            }
        }
    }

    private void logRequest(RequestLoggingModel requestModel) {
        try {
            logger.info("\nREQUEST RECEIVED: " + requestModel.toString(objectMapper));
        } catch (Exception e) {
            logger.error("Error serializing request data: " + e.getMessage());
        }
    }

    private void logResponse(ResponseLoggingModel responseModel) {
        try {
            logger.info("\nRESPONSE SENT: " + responseModel.toString());
        } catch (Exception e) {
            logger.error("Error serializing response data: " + e.getMessage());
        }
    }

    private Map<String, String> convertParamsToStringMap(Map<String, String[]> params) {
        Map<String, String> paramsMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            paramsMap.put(entry.getKey(), String.join(",", entry.getValue()));
        }
        return paramsMap;
    }

    private String getRequestBody(HttpServletRequest request) throws IOException {
        String requestBody = null;
        try {
            requestBody = StreamUtils.copyToString(request.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Error reading request body: " + e.getMessage());
        }
        return requestBody;
    }

    private Map<String, String> getPathVariables(HttpServletRequest request) {
        Map<String, String> pathVariables = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String, String> templateVariables = (Map<String, String>) request.getAttribute(
                "org.springframework.web.servlet.HandlerMapping.uriTemplateVariables");
        if (templateVariables != null) {
            pathVariables.putAll(templateVariables);
        }
        return pathVariables;
    }
}
