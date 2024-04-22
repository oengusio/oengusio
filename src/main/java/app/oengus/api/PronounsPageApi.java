package app.oengus.api;

import app.oengus.entity.model.api.Pronoun;
import app.oengus.configuration.CoreFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(value = "pronouns.page", url = "https://en.pronouns.page/api", configuration = CoreFeignConfiguration.class)
public interface PronounsPageApi {
    @RequestMapping(method = RequestMethod.GET, value = "/pronouns")
    Map<String, Pronoun> getPronouns();
}
