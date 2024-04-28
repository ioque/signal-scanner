package ru.ioque.investfund.application.datasource.integration;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateInstrumentsHandler extends IntegrationHandler<IntegrateInstrumentsCommand> {
    InstrumentRepository instrumentRepository;
    DatasourceRepository datasourceRepository;

    public IntegrateInstrumentsHandler(
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
    protected List<ApplicationLog> businessProcess(IntegrateInstrumentsCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        datasourceProvider
            .fetchInstruments(datasource)
            .stream()
            .map(InstrumentDto::toDetails)
            .distinct()
            .map(details -> Instrument.of(instrumentRepository.nextId(), details))
            .forEach(datasource::addInstrument);
        datasourceRepository.save(datasource);
        return List.of(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("В источнике данных[id=%s] выполнена интеграция инструментов.", datasource.getId())
        ));
    }
}
