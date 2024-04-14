package ru.ioque.investfund.domain.core;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidatorException extends RuntimeException {
    private final List<String> errors;

    public ValidatorException(List<String> erros) {
        this.errors = erros;
    }
}