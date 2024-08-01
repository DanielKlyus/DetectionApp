package com.example.sber_ai.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Animal detection API", version = "1.0.0", description = "Веб-платформа, созданная для детекции животных по фотографиям")
)
@Configuration
public class OpenApiConfig {
}
