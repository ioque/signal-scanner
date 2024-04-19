package ru.ioque.investfund.application.command.handlers.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.command.CommandHandler;
import ru.ioque.investfund.domain.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EnableUpdateInstrumentHandler extends CommandHandler<EnableUpdateInstrumentsCommand> {
    DatasourceRepository datasourceRepository;

    public EnableUpdateInstrumentHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void handleFor(EnableUpdateInstrumentsCommand command) {
        final Datasource datasource = datasourceRepository.getById(command.getDatasourceId());
        datasource.enableUpdate(command.getInstrumentIds());
        datasourceRepository.save(datasource);
    }
}
