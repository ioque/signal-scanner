package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.command.DisableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DisableUpdateInstrumentProcessor extends CommandProcessor<DisableUpdateInstrumentsCommand> {
    DatasourceRepository repository;

    public DisableUpdateInstrumentProcessor(
        Validator validator,
        LoggerFacade loggerFacade,
        DatasourceRepository repository
    ) {
        super(validator, loggerFacade);
        this.repository = repository;
    }

    @Override
    protected void handleFor(DisableUpdateInstrumentsCommand command) {
        final Datasource datasource = repository
            .getBy(command.getDatasourceId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", command.getDatasourceId())
                )
            );
        datasource.disableUpdate(command.getTickers());
        repository.saveDatasource(datasource);
    }
}
