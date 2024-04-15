package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateInstrumentsProcessor extends CommandProcessor<IntegrateInstrumentsCommand> {
    DatasourceProvider datasourceProvider;
    DatasourceRepository repository;

    public IntegrateInstrumentsProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerFacade loggerFacade,
        DatasourceProvider datasourceProvider,
        DatasourceRepository repository
    ) {
        super(dateTimeProvider, validator, loggerFacade);
        this.dateTimeProvider = dateTimeProvider;
        this.datasourceProvider = datasourceProvider;
        this.repository = repository;
    }

    @Override
    protected void handleFor(IntegrateInstrumentsCommand command) {
        final Datasource datasource = getDatasource(command.getDatasourceId());
        datasourceProvider.fetchInstruments(datasource).forEach(datasource::addInstrument);
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
