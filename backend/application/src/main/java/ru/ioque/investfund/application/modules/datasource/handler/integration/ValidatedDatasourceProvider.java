package ru.ioque.investfund.application.modules.datasource.handler.integration;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.intraday.IntradayDataDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class ValidatedDatasourceProvider implements DatasourceProvider {
    private DatasourceProvider datasourceProvider;
    private Validator validator;

    @Override
    public List<InstrumentDto> fetchInstruments(Datasource datasource) {
        List<@Valid InstrumentDto> instrumentDtos = datasourceProvider.fetchInstruments(datasource);
        validate(instrumentDtos);
        return instrumentDtos;
    }

    @Override
    public List<AggregatedHistoryDto> fetchAggregateHistory(Datasource datasource, Instrument instrument) {
        List<@Valid AggregatedHistoryDto> aggregatedHistoryDtos = datasourceProvider.fetchAggregateHistory(
            datasource,
            instrument
        );
        validate(aggregatedHistoryDtos);
        return aggregatedHistoryDtos;
    }

    @Override
    public List<IntradayDataDto> fetchIntradayValues(Datasource datasource, Instrument instrument) {
        List<IntradayDataDto> intradayDataDtos = datasourceProvider.fetchIntradayValues(datasource, instrument);
        validate(intradayDataDtos);
        return intradayDataDtos;
    }

    protected <V> void validate(List<@Valid V> values) {
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
            throw new IntegrationValidationException(errors);
        }
    }
}
