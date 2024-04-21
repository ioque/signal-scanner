package ru.ioque.investfund.application.datasource.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.datasource.dto.history.HistoryValueDto;
import ru.ioque.investfund.domain.datasource.value.AggregateHistory;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@AllArgsConstructor
public class HistoryBatch {
    List<@Valid HistoryValueDto> historyValues;

    public TreeSet<AggregateHistory> getAggregateHistory(Validator validator) {
        final Set<ConstraintViolation<HistoryBatch>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return new TreeSet<>(historyValues.stream().map(HistoryValueDto::toAggregateHistory).distinct().toList());
    }
}
