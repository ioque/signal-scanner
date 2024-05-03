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
import ru.ioque.investfund.application.modules.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.ArrayList;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegisterDatasourceHandler extends CommandHandler<CreateDatasourceCommand> {
    DatasourceRepository datasourceRepository;

    public RegisterDatasourceHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected Result businessProcess(CreateDatasourceCommand command) {
        final Datasource datasource = Datasource
            .builder()
            .id(datasourceRepository.nextId())
            .name(command.getName())
            .url(command.getUrl())
            .description(command.getDescription())
            .instruments(new ArrayList<>())
            .build();
        datasourceRepository.save(datasource);
        return Result.success();
    }
}
