package ru.ioque.investfund.application.modules.datasource.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.SynchronizeDatasource;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.validator.DomainValidator;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;

import java.util.List;
import java.util.TreeSet;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SynchronizeDatasourceHandler extends CommandHandler<SynchronizeDatasource> {
    DatasourceProvider datasourceProvider;
    InstrumentRepository instrumentRepository;
    DatasourceRepository datasourceRepository;

    public SynchronizeDatasourceHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        InstrumentRepository instrumentRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.instrumentRepository = instrumentRepository;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected Result businessProcess(SynchronizeDatasource command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        final List<InstrumentDetail> details = datasourceProvider
            .fetchInstruments(datasource)
            .stream()
            .distinct()
            .toList();
        DomainValidator<InstrumentDetail> domainValidator = new DomainValidator<>(this.validator);
        domainValidator.validate(details);
        details
            .stream()
            .map(detail -> Instrument.builder()
                .id(instrumentRepository.nextId())
                .updatable(false)
                .detail(detail)
                .build()
            )
            .forEach(datasource::addInstrument);
        datasourceRepository.save(datasource);
        return Result.success();
    }
}
