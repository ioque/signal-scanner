package ru.ioque.investfund.application.modules.datasource.handler;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.repository.AggregatedTotalsRepository;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.UpdateAggregatedTotals;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.validator.DomainValidator;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateAggregatedTotalsHandler extends CommandHandler<UpdateAggregatedTotals> {

    DatasourceProvider datasourceProvider;
    DatasourceRepository datasourceRepository;
    AggregatedTotalsRepository aggregatedTotalsRepository;

    public UpdateAggregatedTotalsHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository,
        AggregatedTotalsRepository aggregatedTotalsRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.datasourceRepository = datasourceRepository;
        this.aggregatedTotalsRepository = aggregatedTotalsRepository;
    }

    @Override
    protected Result businessProcess(UpdateAggregatedTotals command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        for (final Instrument instrument : datasource.getUpdatableInstruments()) {
            final LocalDate from = aggregatedTotalsRepository
                .findActualBy(instrument.getId())
                .map(row -> row.getDate().plusDays(1))
                .orElse(dateTimeProvider.nowDate().minusMonths(6));
            final LocalDate to = dateTimeProvider.nowDate().minusDays(1);
            final List<AggregatedTotals> aggregatedTotals = datasourceProvider
                .fetchAggregateHistory(datasource, instrument, from, to)
                .stream()
                .filter(row -> row.isBetween(from, to))
                .toList();
            final DomainValidator<AggregatedTotals> domainValidator = new DomainValidator<>(this.validator);
            domainValidator.validate(aggregatedTotals);
            aggregatedTotalsRepository.saveAll(aggregatedTotals);
        }
        return Result.success();
    }
}
