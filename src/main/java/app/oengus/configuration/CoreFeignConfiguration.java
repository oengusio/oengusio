package app.oengus.configuration;

import feign.RequestInterceptor;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
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
	private ObjectProvider<FeignHttpMessageConverters> messageConverters;

	@Bean
	FormEncoder feignFormEncoder() {
		return new FormEncoder(new SpringEncoder(this.messageConverters));
	}
}
