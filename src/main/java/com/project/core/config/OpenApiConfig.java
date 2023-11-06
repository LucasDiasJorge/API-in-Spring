package com.project.core.config;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
// @SecurityScheme(type = SecuritySchemeType.HTTP,
// bearerFormat = "JWT",
// scheme = "bearer",
// name = "Bearer Authentication",
// description = "POST '../api/v2/auth' to get the token")
public class OpenApiConfig {

    @Bean
    public Info apiInfo() {
        Info info = new Info().title("API").description("CORE API").version("V2");
        return info;
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("CORE API")
                .pathsToMatch("/api/v2/**")
                .addOpenApiCustomiser(new OpenApiCustomiser() {

                    @Override
                    public void customise(OpenAPI openApi) {
                        openApi.info(apiInfo());
                    }

                })
                .build();
    }

    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT").description("Auth route: POST (../api/v2/auth)")));
    }

}
