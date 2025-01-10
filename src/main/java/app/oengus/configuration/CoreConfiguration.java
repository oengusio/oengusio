package app.oengus.configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableScheduling
public class CoreConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .maxAge(3600)
            .exposedHeaders("Location")
            .allowedMethods("GET", "PUT", "OPTIONS", "POST", "DELETE", "PATCH")
            .allowedOrigins("*")
            .allowCredentials(false)
            .allowedHeaders("*");
    }

    @Bean
    public OpenApiCustomizer consumerTypeHeaderOpenAPICustomiser() {
        return (openApi) -> {
            openApi.setInfo(
                new Info()
                    .title("Oengus.IO API documentation")
                    .contact(
                        new Contact()
                            .url("https://oengus.io/")
                    )
                    .license(
                        new License()
                            .name("AGPL v3")
                            .url("https://github.com/esamarathon/oengusio/blob/master/LICENSE")
                    )
                    .version("@OENGUS_VERSION@")
            );
            openApi.setServers(List.of(
                new Server()
                    .url("https://oengus.io/api")
                    .description("Production server"),
                new Server()
                    .url("https://sandbox.oengus.io/api")
                    .description("Sandbox, use for testing the api"),
                new Server()
                    .url("http://localhost:8080")
                    .description("Local instance of the api")
            ));
        };
    }
}
