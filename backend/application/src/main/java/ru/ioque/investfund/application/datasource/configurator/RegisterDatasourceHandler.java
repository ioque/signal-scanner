package ru.ioque.investfund.application.datasource.configurator;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.datasource.command.CreateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;

import java.util.ArrayList;

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
        datasourceRepository.save(
            Datasource
                .builder()
                .id(DatasourceId.from(uuidProvider.generate()))
                .name(command.getName())
                .url(command.getUrl())
                .description(command.getDescription())
                .instruments(new ArrayList<>())
                .build()
        );
    }
}
