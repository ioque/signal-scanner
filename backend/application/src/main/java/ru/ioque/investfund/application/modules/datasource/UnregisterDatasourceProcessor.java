package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.UnregisterDatasourceCommand;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UnregisterDatasourceProcessor extends CommandProcessor<UnregisterDatasourceCommand> {
    DatasourceRepository repository;

    public UnregisterDatasourceProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerFacade loggerFacade,
        DatasourceRepository repository
    ) {
        super(dateTimeProvider, validator, loggerFacade);
        this.repository = repository;
    }

    @Override
    protected void handleFor(UnregisterDatasourceCommand command) {
        executeBusinessProcess(
            () -> repository.deleteDatasource(command.getDatasourceId()),
            "Источник данных[id=%s] удален из системы."
        );
    }
}
