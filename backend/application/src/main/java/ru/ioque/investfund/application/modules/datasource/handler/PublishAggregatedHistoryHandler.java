package ru.ioque.investfund.application.modules.datasource.handler;

import java.time.LocalDate;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.journal.AggregatedTotalsJournal;
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
    AggregatedTotalsJournal aggregatedTotalsJournal;

    public PublishAggregatedHistoryHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        AggregatedTotalsJournal aggregatedTotalsJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.datasourceRepository = datasourceRepository;
        this.aggregatedTotalsJournal = aggregatedTotalsJournal;
    }

    @Override
    protected Result businessProcess(PublishAggregatedHistory command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        for (final Instrument instrument : datasource.getUpdatableInstruments()) {
            final LocalDate from = aggregatedTotalsJournal
                .findActualBy(instrument.getTicker())
                .map(row -> row.getDate().plusDays(1))
                .orElse(dateTimeProvider.nowDate().minusMonths(6));
            final LocalDate to = dateTimeProvider.nowDate().minusDays(1);
            datasourceProvider
                .fetchAggregateHistory(datasource, instrument, from, to)
                .stream()
                .filter(row -> row.isBetween(from, to))
                .forEach(aggregatedTotalsJournal::publish);
        }
        return Result.success();
    }
}
