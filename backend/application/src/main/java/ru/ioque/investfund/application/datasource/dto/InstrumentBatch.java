package ru.ioque.investfund.application.datasource.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.datasource.dto.instrument.InstrumentDto;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;

import java.util.List;

@AllArgsConstructor
public class InstrumentBatch {
    List<@Valid InstrumentDto> instrumentDtos;

    public List<InstrumentDetails> getInstrumentDetails() {
        return instrumentDtos.stream().map(InstrumentDto::toDetails).distinct().toList();
    }
}
