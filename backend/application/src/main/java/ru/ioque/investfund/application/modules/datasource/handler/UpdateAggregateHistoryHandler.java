package ru.ioque.investfund.application.modules.datasource.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.UpdateAggregateHistory;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.TreeSet;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateAggregateHistoryHandler extends CommandHandler<UpdateAggregateHistory> {
    DatasourceProvider datasourceProvider;
    DatasourceRepository datasourceRepository;

    public UpdateAggregateHistoryHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = datasourceProvider;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected Result businessProcess(UpdateAggregateHistory command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        datasource
            .getInstruments()
            .stream()
            .filter(Instrument::isUpdatable)
            .forEach(instrument -> instrument.updateAggregateHistory(
                new TreeSet<>(
                    datasourceProvider.fetchAggregateHistory(datasource, instrument)
                        .stream()
                        .filter(data -> !instrument.contains(data))
                        .toList()
                )
            ));
        datasourceRepository.save(datasource);
        return Result.success();
    }
}
