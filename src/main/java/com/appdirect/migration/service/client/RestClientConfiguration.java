package com.appdirect.migration.service.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfiguration {
	@Value("${appdirect.url}")
	private String appdirectUrl;

	@Bean
	public Client client() {
		return ClientBuilder.newClient();
	}

	@Bean
	public WebTarget webTarget(Client client) {
		return client.target(appdirectUrl);
	}
}
