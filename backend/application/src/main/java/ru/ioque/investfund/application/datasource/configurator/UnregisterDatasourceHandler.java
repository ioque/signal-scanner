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
import ru.ioque.investfund.application.datasource.command.UnregisterDatasourceCommand;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UnregisterDatasourceHandler extends CommandHandler<UnregisterDatasourceCommand> {
    DatasourceRepository datasourceRepository;

    public UnregisterDatasourceHandler(
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
    protected void businessProcess(UnregisterDatasourceCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        datasourceRepository.remove(datasource);
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Источник данных[id=%s] удален.", datasource.getId()),
            command.getTrack()
        ));
    }
}
