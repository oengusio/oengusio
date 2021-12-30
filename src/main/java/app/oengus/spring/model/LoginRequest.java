package app.oengus.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {

	private String service;
	private String code;

	public String getService() {
		return this.service;
	}

	public void setService(final String service) {
		this.service = service;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
}
