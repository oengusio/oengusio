package app.oengus.application.api;

import app.oengus.configuration.CoreFeignConfiguration;
import app.oengus.domain.AccessToken;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(value = "twitchOauth", url = "https://id.twitch.tv", configuration = CoreFeignConfiguration.class)
public interface TwitchOauthApi {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestMapping(method = RequestMethod.POST, value = "/oauth2/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    AccessToken getAccessToken(@RequestBody Map<String, ?> body);


}
