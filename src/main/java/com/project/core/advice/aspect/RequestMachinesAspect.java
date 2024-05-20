package com.project.core.advice.aspect;

import com.project.core.annotation.RequestMachines;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Aspect
@Component
public class RequestMachinesAspect {

    private final RestTemplate restTemplate;

    public RequestMachinesAspect(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Around("@annotation(requestMachines)")
    public Object around(ProceedingJoinPoint joinPoint, RequestMachines requestMachines) throws Throwable {
        String url = requestMachines.url();

        // Faça a chamada HTTP GET
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Imprima a resposta (ou lide com ela de outra forma, conforme necessário)
        System.out.println("Response from GET request: " + response.getBody());

        // Continue a execução do método original
        return joinPoint.proceed();
    }
}
