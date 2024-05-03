package ru.ioque.investfund.application.modules.datasource.handler.configurator;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.datasource.command.UpdateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateDatasourceHandler extends CommandHandler<UpdateDatasourceCommand> {
    DatasourceRepository datasourceRepository;

    public UpdateDatasourceHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected Result businessProcess(UpdateDatasourceCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getId());
        datasource.updateName(command.getName());
        datasource.updateUrl(command.getUrl());
        datasource.updateDescription(command.getDescription());
        datasourceRepository.save(datasource);
        return Result.success();
    }
}