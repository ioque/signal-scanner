package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.UUID;

public abstract class DatasourceProcessor<C> extends CommandProcessor<C> {
    DatasourceRepository datasourceRepository;

    public DatasourceProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected abstract void handleFor(C command);

    protected Datasource getDatasource(UUID datasourceId) {
        return datasourceRepository
            .getBy(datasourceId)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", datasourceId)
                )
            );
    }
}
