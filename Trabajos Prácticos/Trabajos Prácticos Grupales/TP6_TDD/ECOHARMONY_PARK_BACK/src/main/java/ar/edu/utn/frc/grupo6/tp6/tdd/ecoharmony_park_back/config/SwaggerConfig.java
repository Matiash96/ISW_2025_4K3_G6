package ar.edu.utn.frc.grupo6.tp6.tdd.ecoharmony_park_back.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EcoHarmony Park API")
                        .description("API REST para el sistema de gestión del parque ecológico EcoHarmony Park. " +
                                "Permite gestionar inscripciones de visitantes a actividades programadas, " +
                                "consultar tallas disponibles, actividades y horarios.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Grupo 6 - ISW 2025")
                                .email("grupo6@ecoharmonypark.com")
                                .url("https://github.com/grupo6/ecoharmony-park")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo local")
                ));
    }
}
