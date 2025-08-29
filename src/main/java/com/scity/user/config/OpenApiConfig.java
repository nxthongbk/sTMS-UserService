package com.scity.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =
        @Info(
                contact = @Contact(name = "Tma Solution", email = "youremail@tma.com.vn", url = ""),
                description = "OpenApi documentation for Spring Boot scity",
                title = "OpenApi specification - TMA",
                version = "1.0",
                license = @License(name = "Licence name", url = "https://some-url.com"),
                termsOfService = "Terms of service"),
        servers = {
                @Server(
                        url = "${app.gateway.domain-name}${server.servlet.context-path}"),
                @Server(url = "http://localhost:${server.port}${server.servlet.context-path}")
        },
        security = {@SecurityRequirement(name = "bearerAuth")})
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
