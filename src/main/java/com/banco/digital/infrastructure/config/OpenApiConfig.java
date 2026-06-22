package com.banco.digital.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Banco Digital")
                        .version("1.0.0")
                        .description("""
                                API REST para banco digital com suporte a:
                                - Cadastro e consulta de clientes
                                - Gestão de contas bancárias
                                - Transferências entre contas com controle de concorrência
                                - Consulta de extrato e movimentações
                                - Notificações assíncronas após transferências
                                """)
                        .contact(new Contact()
                                .name("Banco Digital")
                                .email("dev@banco.digital"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
