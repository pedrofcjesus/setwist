package com.setwist.backend.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SETWIST API")
                        .version("1.0.0")
                        .description(
                                "Documentação interativa dos endpoints da API SETWIST (Gestão de Bandas, Músicas e Setlists)."));
    }
}
