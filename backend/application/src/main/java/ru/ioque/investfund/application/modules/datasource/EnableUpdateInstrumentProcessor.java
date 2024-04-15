package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EnableUpdateInstrumentProcessor extends CommandProcessor<EnableUpdateInstrumentsCommand> {
    DatasourceRepository repository;

    public EnableUpdateInstrumentProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerFacade loggerFacade,
        DatasourceRepository repository
    ) {
        super(dateTimeProvider, validator, loggerFacade);
        this.repository = repository;
    }

    @Override
    protected void handleFor(EnableUpdateInstrumentsCommand command) {
        final Datasource datasource = getDatasource(command.getDatasourceId());
        datasource.enableUpdate(command.getTickers());
        repository.saveDatasource(datasource);
    }

    private Datasource getDatasource(UUID datasourceId) {
        return repository
            .getBy(datasourceId)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", datasourceId)
                )
            );
    }
}
