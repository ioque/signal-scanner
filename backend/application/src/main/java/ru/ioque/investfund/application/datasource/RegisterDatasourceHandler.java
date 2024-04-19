package ru.ioque.investfund.application.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.CommandHandler;
import ru.ioque.investfund.domain.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegisterDatasourceHandler extends CommandHandler<CreateDatasourceCommand> {
    DatasourceRepository datasourceRepository;
    UUIDProvider uuidProvider;

    public RegisterDatasourceHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void businessProcess(CreateDatasourceCommand command) {
        datasourceRepository.save(Datasource.of(DatasourceId.from(uuidProvider.generate()), command));
    }
}
