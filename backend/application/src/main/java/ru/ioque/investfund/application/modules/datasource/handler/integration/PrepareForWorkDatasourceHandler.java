package ru.ioque.investfund.application.modules.datasource.handler.integration;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.PrepareForWorkDatasource;
import ru.ioque.investfund.application.modules.datasource.handler.integration.dto.history.AggregatedHistoryDto;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.TreeSet;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PrepareForWorkDatasourceHandler extends IntegrationHandler<PrepareForWorkDatasource> {
    InstrumentRepository instrumentRepository;
    DatasourceRepository datasourceRepository;

    public PrepareForWorkDatasourceHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        InstrumentRepository instrumentRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider, datasourceProvider);
        this.instrumentRepository = instrumentRepository;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected Result businessProcess(PrepareForWorkDatasource command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        datasourceProvider
            .fetchInstruments(datasource)
            .stream()
            .distinct()
            .map(dto -> Instrument.builder()
                .id(instrumentRepository.nextId())
                .ticker(Ticker.from(dto.getTicker()))
                .updatable(false)
                .type(dto.getType())
                .details(dto.toDetails())
                .aggregateHistories(new TreeSet<>())
                .build()
            )
            .forEach(datasource::addInstrument);
        datasource.getInstruments().forEach(instrument -> {
            instrument.updateAggregateHistory(
                new TreeSet<>(
                    datasourceProvider.fetchAggregateHistory(datasource, instrument)
                        .stream()
                        .map(AggregatedHistoryDto::toAggregateHistory)
                        .filter(data -> !instrument.contains(data))
                        .toList()
                )
            );
        });
        datasourceRepository.save(datasource);
        return Result.success();
    }
}
