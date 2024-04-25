package ru.ioque.investfund.application.datasource.configurator;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.datasource.command.EnableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EnableUpdateInstrumentHandler extends CommandHandler<EnableUpdateInstrumentsCommand> {
    DatasourceRepository datasourceRepository;

    public EnableUpdateInstrumentHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider);
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void businessProcess(EnableUpdateInstrumentsCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        datasource.enableUpdate(command.getTickers());
        datasourceRepository.save(datasource);
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format(
                "В источник данных[id=%s] активировано обновление торговых данных для инструментов с тикерами %s",
                datasource.getId(),
                command.getTickers()
            ),
            command.getTrack()
        ));
    }
}
