package com.modakdev.product_catalog_module.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GenericModelTrainerClient {

    private final RestTemplate restTemplate;

    private final String baseUrl;


    public GenericModelTrainerClient(@Value("${flask.base-url}")String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    public Object getSingleQueryResponse(int pid){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/get-product")
                .queryParam("id", pid)
                .toUriString();

        ResponseEntity<Object> response = restTemplate.getForEntity(requestUrl, Object.class);
        return response.getBody();
    }
}
