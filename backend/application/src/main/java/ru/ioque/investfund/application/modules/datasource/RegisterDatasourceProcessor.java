package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegisterDatasourceProcessor extends CommandProcessor<CreateDatasourceCommand> {
    UUIDProvider uuidProvider;
    DatasourceRepository repository;

    public RegisterDatasourceProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerFacade loggerFacade,
        UUIDProvider uuidProvider,
        DatasourceRepository repository
    ) {
        super(dateTimeProvider, validator, loggerFacade);
        this.uuidProvider = uuidProvider;
        this.repository = repository;
    }

    @Override
    protected void handleFor(CreateDatasourceCommand command) {
        UUID newDatasourceId = uuidProvider.generate();
        executeBusinessProcess(
            () -> repository.saveDatasource(Datasource.of(newDatasourceId, command)),
            String.format("Зарегистрирован источник данных[id=%s]", newDatasourceId)
        );
    }
}
