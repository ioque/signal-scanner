package ru.ioque.investfund.adapters.exchagne.moex.client;


public class RestClientException extends RuntimeException {
    public RestClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

