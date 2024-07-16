package com.project.core.utils;

import com.project.core.service.integration.IntegrationInterceptorService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class IntegrationInterceptor implements HandlerInterceptor {

    public IntegrationInterceptorService integrationInterceptorService;

    public IntegrationInterceptor(IntegrationInterceptorService integrationInterceptorService) {
        this.integrationInterceptorService = integrationInterceptorService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        System.out.println("PreHandler");
        return true;
    }


    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("PostHandler");
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull Object handler, Exception exception) throws Exception {
        System.out.println("Request and Response is completed for specific controller");
        integrationInterceptorService.integrationMirror(request,response,handler);
    }
}
