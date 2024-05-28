package ru.ioque.investfund.application.modules.datasource.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.journal.AggregatedHistoryJournal;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.PublishAggregatedHistory;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PublishAggregatedHistoryHandler extends CommandHandler<PublishAggregatedHistory> {
    DatasourceProvider datasourceProvider;
    DatasourceRepository datasourceRepository;
    AggregatedHistoryJournal aggregatedHistoryJournal;

    public PublishAggregatedHistoryHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        AggregatedHistoryJournal aggregatedHistoryJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.datasourceRepository = datasourceRepository;
        this.aggregatedHistoryJournal = aggregatedHistoryJournal;
    }

    @Override
    protected Result businessProcess(PublishAggregatedHistory command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        for (Instrument instrument : datasource.getUpdatableInstruments()) {
            datasourceProvider
                .fetchAggregateHistory(datasource, instrument)
                .forEach(aggregatedHistoryJournal::publish);
        }
        return Result.success();
    }
}
