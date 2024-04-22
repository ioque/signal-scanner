package ru.ioque.investfund.application.datasource.integration;

import jakarta.validation.Validator;
import ru.ioque.investfund.application.CommandHandler;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;

public abstract class IntegrationHandler<C> extends CommandHandler<C> {
    protected DatasourceProvider datasourceProvider;

    public IntegrationHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceProvider = new ValidatedDatasourceProvider(datasourceProvider, validator);
    }

    protected abstract void businessProcess(C command);
}