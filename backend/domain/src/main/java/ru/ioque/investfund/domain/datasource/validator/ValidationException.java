package ru.ioque.investfund.domain.datasource.validator;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidationError> validationErrors;

    public ValidationException(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
