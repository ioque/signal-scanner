package ru.ioque.investfund.application.modules.datasource.handler;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.journal.IntradayJournal;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.PublishIntradayData;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishIntradayDataHandler extends CommandHandler<PublishIntradayData> {
    DatasourceProvider datasourceProvider;
    IntradayJournal intradayJournal;
    DatasourceRepository datasourceRepository;
    Map<Ticker, Long> numbers = new HashMap<>();

    public PublishIntradayDataHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        IntradayJournal intradayJournal,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.intradayJournal = intradayJournal;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected Result businessProcess(PublishIntradayData command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        for (final Instrument instrument : datasource.getUpdatableInstruments()) {
            final Long from = numbers.getOrDefault(instrument.getTicker(), 0L);
            datasourceProvider
                .fetchIntradayValues(datasource, instrument, from)
                .stream()
                .filter(data -> data.getNumber() > from)
                .forEach(intradayData -> {
                    intradayJournal.publish(intradayData);
                    numbers.put(instrument.getTicker(), intradayData.getNumber());
                });
        }
        datasourceRepository.save(datasource);
        return Result.success();
    }
}
