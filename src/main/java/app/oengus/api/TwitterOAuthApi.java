package app.oengus.api;

import app.oengus.entity.model.api.twitter.TwitterDataResponse;
import app.oengus.entity.model.api.twitter.TwitterUser;
import app.oengus.spring.CoreFeignConfiguration;
import app.oengus.spring.model.AccessToken;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(value = "twitter", url = "https://api.twitter.com/2", configuration = CoreFeignConfiguration.class)
public interface TwitterOAuthApi {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestMapping(method = RequestMethod.POST, value = "/oauth2/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    AccessToken getAccessToken(@RequestHeader("Authorization") String token, @RequestBody Map<String, ?> body);

    @RequestMapping(method = RequestMethod.GET, value = "/users/me")
    TwitterDataResponse<TwitterUser> getCurrentUser(@RequestHeader("Authorization") String token);
}
