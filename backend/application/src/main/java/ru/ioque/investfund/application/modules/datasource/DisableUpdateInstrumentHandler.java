package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.modules.CommandHandler;
import ru.ioque.investfund.domain.datasource.command.DisableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DisableUpdateInstrumentHandler extends CommandHandler<DisableUpdateInstrumentsCommand> {
    DatasourceRepository datasourceRepository;

    public DisableUpdateInstrumentHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void handleFor(DisableUpdateInstrumentsCommand command) {
        final Datasource datasource = datasourceRepository.getById(command.getDatasourceId());
        datasource.disableUpdate(command.getTickers());
        datasourceRepository.save(datasource);
    }
}
