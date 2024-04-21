package ru.ioque.investfund.application.datasource.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.datasource.dto.instrument.InstrumentDto;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class InstrumentBatch {
    List<@Valid InstrumentDto> instrumentDtos;

    public List<InstrumentDetails> getInstrumentDetails(Validator validator) {
        final Set<ConstraintViolation<InstrumentBatch>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return instrumentDtos.stream().map(InstrumentDto::toDetails).distinct().toList();
    }
}
