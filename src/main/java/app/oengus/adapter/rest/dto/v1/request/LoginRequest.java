package app.oengus.adapter.rest.dto.v1.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {
    @NotBlank
	private String service;

    @NotBlank
	private String code;

    @JsonIgnore
    @AssertTrue
    public boolean serviceSupported() {
        return List.of("discord", "twitch").contains(this.service);
    }
}
