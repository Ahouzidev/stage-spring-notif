package com.example.notifications.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Notification API", version = "1.0"),
        security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "keycloak")
)
public class OpenApiConfig {

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Bean
    public OpenAPI customOpenAPI() {
        String authUrl = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/auth";
        String tokenUrl = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";

        SecurityScheme oauthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .in(SecurityScheme.In.HEADER)
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl(authUrl)
                                .tokenUrl(tokenUrl)
                        )
                )
                .name("keycloak");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("keycloak", oauthScheme))
                .addSecurityItem(new SecurityRequirement().addList("keycloak"));
    }
}
