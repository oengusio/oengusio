package app.oengus.api;

import app.oengus.entity.model.api.DataList;
import app.oengus.entity.model.api.TwitchUser;
import app.oengus.configuration.CoreFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "twitch", url = "https://api.twitch.tv/helix", configuration = CoreFeignConfiguration.class)
public interface TwitchApi {

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    DataList<TwitchUser> getCurrentUser(@RequestHeader("Authorization") String token,
                                        @RequestHeader("Client-ID") String clientId);

    @RequestMapping(method = RequestMethod.GET, value = "/users/{id}")
    DataList<TwitchUser> getUser(@RequestHeader("Authorization") String token,
                                 @RequestHeader("Client-ID") String clientId, @PathVariable("id") String id);


}
