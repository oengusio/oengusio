package app.oengus.api;

import app.oengus.entity.model.Donation;
import app.oengus.spring.CoreFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;

@FeignClient(name = "donationWebhook", url = "https://placeholder.com", configuration = CoreFeignConfiguration.class)
public interface DonationWebhook {

	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<?> sendDonationEvent(URI baseURI, @RequestBody Donation donation);

}
