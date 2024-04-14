package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.UnregisterDatasourceCommand;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UnregisterDatasourceProcessor extends CommandProcessor<UnregisterDatasourceCommand> {
    DatasourceRepository repository;

    public UnregisterDatasourceProcessor(
        Validator validator,
        LoggerFacade loggerFacade, DatasourceRepository repository
    ) {
        super(validator, loggerFacade);
        this.repository = repository;
    }

    @Override
    protected void handleFor(UnregisterDatasourceCommand command) {
        repository.deleteDatasource(command.getDatasourceId());
    }
}
