package ru.ioque.apitest.client;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestTemplateFacade {
    final RestTemplate restTemplate;
    @Value("${variables.api_host}")
    String host;

    public RestTemplateFacade(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected String url(String path) {
        return host + path;
    }

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

    private HttpEntity<String> getRequestEntity(String body) {
        return new HttpEntity<>(body, headers());
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
