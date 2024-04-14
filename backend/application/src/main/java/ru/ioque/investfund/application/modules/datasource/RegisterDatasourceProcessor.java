package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegisterDatasourceProcessor extends CommandProcessor<CreateDatasourceCommand> {
    UUIDProvider uuidProvider;
    DatasourceRepository repository;

    public RegisterDatasourceProcessor(
        Validator validator,
        LoggerFacade loggerFacade,
        UUIDProvider uuidProvider,
        DatasourceRepository repository
    ) {
        super(validator, loggerFacade);
        this.uuidProvider = uuidProvider;
        this.repository = repository;
    }

    @Override
    protected void handleFor(CreateDatasourceCommand command) {
        repository.saveDatasource(Datasource.from(uuidProvider.generate(), command));
    }
}
