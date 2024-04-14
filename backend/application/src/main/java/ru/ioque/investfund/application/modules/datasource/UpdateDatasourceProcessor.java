package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.UpdateDatasourceCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateDatasourceProcessor extends CommandProcessor<UpdateDatasourceCommand> {
    DatasourceRepository repository;

    public UpdateDatasourceProcessor(
        Validator validator,
        LoggerFacade loggerFacade,
        DatasourceRepository repository
    ) {
        super(validator, loggerFacade);
        this.repository = repository;
    }

    @Override
    protected void handleFor(UpdateDatasourceCommand command) {
        final Datasource datasource = repository
            .getBy(command.getId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", command.getId())
                )
            );
        datasource.update(command);
        repository.saveDatasource(datasource);
    }
}
