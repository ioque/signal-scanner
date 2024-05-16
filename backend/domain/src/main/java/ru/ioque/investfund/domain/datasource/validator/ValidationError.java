package ru.ioque.investfund.domain.datasource.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class ValidationError {
    String inputData;
    List<String> errors;
}
