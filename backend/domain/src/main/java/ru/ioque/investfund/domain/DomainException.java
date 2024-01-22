package ru.ioque.investfund.domain;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
