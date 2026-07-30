package com.aitovavi.fleetops.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fleetOpsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FleetOps API")
                        .description(
                                "REST API for customer and shipment management"
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aitov Avi")));
    }
}