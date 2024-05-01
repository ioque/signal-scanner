package ru.ioque.investfund.application.datasource.configurator;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.api.command.Result;
import ru.ioque.investfund.application.datasource.command.DisableUpdateInstrumentsCommand;
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
    protected Result businessProcess(DisableUpdateInstrumentsCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        datasource.disableUpdate(command.getTickers());
        datasourceRepository.save(datasource);
        return Result.success();
    }
}
