package app.oengus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties
@EnableTransactionManagement
public class OengusApplication {

	public static void main(final String[] args) {
		SpringApplication.run(OengusApplication.class, args);
	}

}
