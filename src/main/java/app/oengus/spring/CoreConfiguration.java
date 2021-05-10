package app.oengus.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableScheduling
public class CoreConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
            // override the default base path to make sure it displays properly on the docs page
            .pathProvider(new PathProvider() {
                // this was removed in version 2.10, wtf
                @Override
                public String getApplicationBasePath() {
                    return "/api/";
                }

                @Override
                public String getOperationPath(String operationPath) {
                    return operationPath;
                }

                @Override
                public String getResourceListingPath(String groupName, String apiDeclaration) {
                    return groupName + apiDeclaration;
                }
            })
            .select()
            .apis(RequestHandlerSelectors.basePackage("app.oengus.web"))
            .paths(PathSelectors.any())
            .build();
    }
}
