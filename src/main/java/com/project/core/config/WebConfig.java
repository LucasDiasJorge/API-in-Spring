package com.project.core.config;

import com.project.core.utils.IntegrationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private IntegrationInterceptor integrationInterceptor;

    public WebConfig(IntegrationInterceptor integrationInterceptor) {
        this.integrationInterceptor = integrationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(integrationInterceptor)
                .addPathPatterns("/core/ping", "/item/*","/item"); // Apply to specific controller's URL patterns

    }
}
