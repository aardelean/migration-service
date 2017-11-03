package com.appdirect.migration.service.client;

import java.io.IOException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestClient {
	private static final Logger log = LoggerFactory.getLogger(RestClient.class);
	private static final String PAGE_SIZE_PARAM_NAME = "size";
	private static final String PAGE_NO_PARAM_NAME = "page";
	private static final int PAGE_SIZE = 1000;
	private ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private WebTarget webTarget;

	public void invokeEndpointWithId(String path, Long id) {
		Response response = webTarget.path(path).request().build("POST").invoke();
		if (response.getStatus() != HttpStatus.OK.value()) {
			try {
				RuntimeException exception = objectMapper.readValue(response.readEntity(String.class), RuntimeException.class);
				log.warn("Could not migrate entity with id:" + id);
				throw exception;
			} catch (IOException e) {
				log.error("Could not deserialize exception ", e);
			}
		}
		log.debug("Successfully migrated entity with id: " + id);
	}

	public <T> Page<T> fetchEntities(String path, int pageNo) throws IOException {
		Response response =  webTarget
				.path(path)
				.queryParam(PAGE_NO_PARAM_NAME, pageNo)
				.queryParam(PAGE_SIZE_PARAM_NAME, PAGE_SIZE)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get();

		String output = response.readEntity(String.class);
		return objectMapper.readValue(output, RestPageImpl.class).pageImpl();
	}
}
