package com.modakdev.product_catalog_module.api.client;

import com.modakdev.request.TrainingRequest;
import com.modakdev.response.TrainModelResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class GenericModelTrainerClient {

    private final RestTemplate restTemplate;

    private final String baseUrl;


    public GenericModelTrainerClient(@Value("${flask.base-url}")String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    public Object getSingleProduct(int pid){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/get-product")
                .queryParam("id", pid)
                .toUriString();

        ResponseEntity<Object> response = restTemplate.getForEntity(requestUrl, Object.class);
        return response.getBody();
    }

    public Object getAllProducts(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/get-all-products")
                .toUriString();

        ResponseEntity<Object> response = restTemplate.getForEntity(requestUrl, Object.class);
        return response.getBody();
    }

    public ResponseEntity<InputStreamResource> callCorrelationImgPlotApi(String modelName, String trainsetPath) throws IOException {
        String requestUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/get-correlation-matrix")
                .queryParam("model_name", modelName)
                .queryParam("trainset_name", trainsetPath)
                .toUriString();

        HttpGet httpGet = new HttpGet(requestUrl);

        // Execute the request and get the response
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            byte[] imageBytes = EntityUtils.toByteArray(entity);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"correlation_matrix.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new InputStreamResource(bis));
        } else {
            return ResponseEntity.status(response.getStatusLine().getStatusCode()).build();
        }
    }

    public Object trainModel(TrainingRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/train-model")
                .toUriString();

        ResponseEntity<Object> response = restTemplate.postForEntity(requestUrl, request, Object.class);
        return response.getBody();
    }
}
