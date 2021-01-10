package app.oengus.service;

import app.oengus.entity.model.Donation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OengusWebhookService {

    @Autowired
    private ObjectMapper mapper;

    public void sendDonationEvent(final String url, final Donation donation) {
        //
    }
}
