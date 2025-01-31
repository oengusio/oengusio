package app.oengus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableFeignClients
@EnableTransactionManagement
@EnableConfigurationProperties
@SpringBootApplication(exclude= { UserDetailsServiceAutoConfiguration.class })
public class OengusApplication {
    public static void main(final String[] args) {
        SpringApplication.run(OengusApplication.class, args);
    }
}
