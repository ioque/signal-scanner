package ru.ioque.investfund.adapters.rest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Signal Scanner System API",
        description = "Signal Scanner System", version = "1.0.0",
        contact = @Contact(
            name = "Telegin Kirill Igorevich",
            email = "dev.arven@gmail.com"
        )
    ),
    servers = {
        @Server(
            description = "Local",
            url = "http://localhost:8080"
        )
    }
)
public class SwaggerConfig {
}
