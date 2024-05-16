package ru.ioque.investfund.domain.datasource.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DomainValidator<V> {
    Validator validator;

    public void validate(List<@Valid V> values) {
        final List<ValidationError> errors = new ArrayList<>();
        for (V value : values) {
            Set<ConstraintViolation<V>> constraintViolations = validator.validate(value);
            if (!constraintViolations.isEmpty()) {
                errors.add(
                    new ValidationError(
                        value.toString(),
                        constraintViolations.stream().map(ConstraintViolation::getMessage).toList()
                    )
                );
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
