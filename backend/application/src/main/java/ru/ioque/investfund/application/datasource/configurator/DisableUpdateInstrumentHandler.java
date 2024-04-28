package ru.ioque.investfund.application.datasource.configurator;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.datasource.command.DisableUpdateInstrumentsCommand;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.List;

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
    protected List<ApplicationLog> businessProcess(DisableUpdateInstrumentsCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        datasource.disableUpdate(command.getTickers());
        datasourceRepository.save(datasource);
        return List.of(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format(
                "В источник данных[id=%s] отключено обновление торговых данных для инструментов с тикерами %s",
                datasource.getId(),
                command.getTickers()
            )
        ));
    }
}
