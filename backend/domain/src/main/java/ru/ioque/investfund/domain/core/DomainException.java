package ru.ioque.investfund.domain.core;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
