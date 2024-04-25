package ru.ioque.investfund.application.datasource.integration;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.datasource.integration.dto.instrument.InstrumentDto;
import ru.ioque.investfund.application.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateInstrumentsHandler extends IntegrationHandler<IntegrateInstrumentsCommand> {
    DatasourceRepository datasourceRepository;

    public IntegrateInstrumentsHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider, datasourceProvider);
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void businessProcess(IntegrateInstrumentsCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        final List<InstrumentDetails> detailsList = datasourceProvider
            .fetchInstruments(datasource)
            .stream()
            .map(InstrumentDto::toDetails)
            .distinct()
            .toList();
        final List<Instrument> instruments = detailsList
            .stream()
            .map(details -> Instrument.of(InstrumentId.from(uuidProvider.generate()), details))
            .toList();
        datasource.integrateInstruments(instruments);
        datasourceRepository.save(datasource);
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("В источнике данных[id=%s] выполнена интеграция инструментов.", datasource.getId()),
            command.getTrack()
        ));
    }
}
