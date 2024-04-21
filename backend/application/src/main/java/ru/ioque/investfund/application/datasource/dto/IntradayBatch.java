package ru.ioque.investfund.application.datasource.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.datasource.dto.intraday.IntradayValueDto;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayValue;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@AllArgsConstructor
public class IntradayBatch {
    List<@Valid IntradayValueDto> intradayValueDtos;

    public TreeSet<IntradayValue> getIntradayValues(Validator validator) {
        final Set<ConstraintViolation<IntradayBatch>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return new TreeSet<>(intradayValueDtos.stream().map(IntradayValueDto::toIntradayValue).distinct().toList());
    }
}
