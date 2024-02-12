package ru.ioque.acceptance.adapters.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateFacade {
    @Value("${variables.api_url}")
    String url;
    @Autowired
    RestTemplate restTemplate;

    public <T> T get(String path, Class<T> responseType) {
        try {
            return restTemplate.getForObject(url(path), responseType);
        } catch (RuntimeException ex) {
            throw new RestClientException(ex.getMessage());
        }
    }

    public void post(String path, String data) {
        try {
            restTemplate.postForEntity(url(path), httpEntity(data), String.class);
        } catch (RuntimeException ex) {
            throw new RestClientException(ex.getMessage());
        }
    }

    public <T> T patch(String path, String body, Class<T> responseType) {
        try {
            return restTemplate.patchForObject(url(path), getRequestEntity(body), responseType);
        } catch (RuntimeException ex) {
            throw new RestClientException(ex.getMessage());
        }
    }

    public void delete(String path) {
        try {
            restTemplate.delete(url(path));
        } catch (RuntimeException ex) {
            throw new RestClientException(ex.getMessage());
        }
    }

    public void put(String path, String body) {
        try {
            restTemplate.exchange(url(path), HttpMethod.PUT, getRequestEntity(body), String.class);
        } catch (RuntimeException ex) {
            throw new RestClientException(ex.getMessage());
        }
    }

    private HttpEntity<String> httpEntity(String data) {
        return getRequestEntity(data);
    }

    private String url(String path) {
        return url + path;
    }

    private HttpEntity<String> getRequestEntity(String body) {
        return new HttpEntity<>(body, headers());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
