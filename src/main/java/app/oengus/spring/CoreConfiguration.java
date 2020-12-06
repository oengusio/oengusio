package app.oengus.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableSwagger2
@EnableScheduling
public class CoreConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		// TODO: double check this
//		registry.addResourceHandler("/assets/**/*")
//		        .addResourceLocations("classpath:/static/assets/")
//		        .setCacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES));
//		registry.addResourceHandler("/index.html")
//		        .addResourceLocations("classpath:/static/index.html")
//		        .setCacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES));
//		registry.addResourceHandler("/**/*")
//		        .addResourceLocations("classpath:/static/")
//		        .setCacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
//		        .resourceChain(true)
//		        .addResolver(new PathResourceResolver() {
//			        @Override
//			        protected Resource getResource(final String resourcePath, final Resource location)
//					        throws IOException {
//				        final Resource requestedResource = location.createRelative(resourcePath);
//				        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource :
//						        new ClassPathResource("/static/index.html");
//			        }
//		        });
		registry.addResourceHandler("swagger-ui.html")
		        .addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**")
		        .addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("app.oengus.web"))
				.paths(PathSelectors.any())
				.build();
	}
}
