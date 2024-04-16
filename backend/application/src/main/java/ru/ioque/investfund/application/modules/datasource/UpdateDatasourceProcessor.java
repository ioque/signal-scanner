package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.datasource.command.UpdateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateDatasourceProcessor extends DatasourceProcessor<UpdateDatasourceCommand> {
    DatasourceRepository datasourceRepository;

    public UpdateDatasourceProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider, datasourceRepository);
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void handleFor(UpdateDatasourceCommand command) {
        final Datasource datasource = getDatasource(command.getId());
        datasource.update(command);
        datasourceRepository.save(datasource);
    }
}
