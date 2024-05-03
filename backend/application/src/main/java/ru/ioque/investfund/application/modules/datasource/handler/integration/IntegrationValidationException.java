package ru.ioque.investfund.application.modules.datasource.handler.integration;

import lombok.Getter;

import java.util.List;

@Getter
public class IntegrationValidationException extends RuntimeException {
    private final List<ValidationError> validationErrors;

    public IntegrationValidationException(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
