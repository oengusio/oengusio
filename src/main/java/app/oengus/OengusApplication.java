package app.oengus;

import app.oengus.service.LanguageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Locale;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties
@EnableTransactionManagement
@CrossOrigin(maxAge = 3600)
public class OengusApplication {

    public static void main(final String[] args) {

        /*System.out.println(
            Locale.forLanguageTag("zh-TW").getDisplayName()
        );


        System.out.println(
            new LanguageService().searchLanguages("Nieder", Locale.GERMAN)
        );*/

        SpringApplication.run(OengusApplication.class, args);
    }
}
