package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.datasource.command.DisableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DisableUpdateInstrumentProcessor extends DatasourceProcessor<DisableUpdateInstrumentsCommand> {
    public DisableUpdateInstrumentProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider, datasourceRepository);
    }

    @Override
    protected void handleFor(DisableUpdateInstrumentsCommand command) {
        final Datasource datasource = getDatasource(command.getDatasourceId());
        datasource.disableUpdate(command.getTickers());
        datasourceRepository.save(datasource);
    }
}
