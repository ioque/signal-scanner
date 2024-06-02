package ru.ioque.investfund.application.modules.datasource.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.IntradayDataJournal;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.validator.DomainValidator;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishIntradayDataHandler extends CommandHandler<PublishIntradayData> {
    DatasourceProvider datasourceProvider;
    IntradayDataJournal intradayDataJournal;
    DatasourceRepository datasourceRepository;
    Map<Ticker, Long> numbers = new HashMap<>();

    public PublishIntradayDataHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        IntradayDataJournal intradayDataJournal,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.intradayDataJournal = intradayDataJournal;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected Result businessProcess(PublishIntradayData command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        final DomainValidator<IntradayData> domainValidator = new DomainValidator<>(this.validator);
        for (final Instrument instrument : datasource.getUpdatableInstruments()) {
            final Long from = numbers.getOrDefault(instrument.getTicker(), 0L);
            final List<IntradayData> intradayDataList = datasourceProvider
                .fetchIntradayValues(datasource, instrument, from)
                .stream()
                .filter(data -> data.getNumber() > from)
                .toList();
            for (IntradayData intradayData : intradayDataList) {
                domainValidator.validate(intradayData);
                numbers.put(intradayData.getTicker(), intradayData.getNumber());
                intradayDataJournal.publish(intradayData);
            }
        }
        datasourceRepository.save(datasource);
        return Result.success();
    }
}
