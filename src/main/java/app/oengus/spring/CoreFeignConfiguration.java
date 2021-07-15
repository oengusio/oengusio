package app.oengus.spring;

import feign.RequestInterceptor;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreFeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return (requestTemplate) -> requestTemplate.header("User-Agent", "oengus.io");
    }

	@Autowired
	private ObjectFactory<HttpMessageConverters> messageConverters;

	@Bean
	FormEncoder feignFormEncoder() {
		return new FormEncoder(new SpringEncoder(this.messageConverters));
	}
}
