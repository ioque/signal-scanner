package ru.ioque.acceptance.adapters.client;

public class RestClientException extends RuntimeException {
    public RestClientException(String message) {
        super(message);
    }
}
