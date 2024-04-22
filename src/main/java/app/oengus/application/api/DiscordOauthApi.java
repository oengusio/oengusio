package app.oengus.application.api;

import app.oengus.configuration.CoreFeignConfiguration;
import app.oengus.domain.AccessToken;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(value = "discord-oauth", url = "https://discord.com/api/oauth2", configuration = CoreFeignConfiguration.class)
public interface DiscordOauthApi {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestMapping(method = RequestMethod.POST, value = "/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    AccessToken getAccessToken(@RequestBody Map<String, ?> body);
}
