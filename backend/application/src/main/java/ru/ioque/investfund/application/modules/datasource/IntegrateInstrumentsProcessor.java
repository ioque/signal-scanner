package ru.ioque.investfund.application.modules.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateInstrumentsProcessor extends DatasourceProcessor<IntegrateInstrumentsCommand> {
    DatasourceProvider datasourceProvider;

    public IntegrateInstrumentsProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider, datasourceRepository);
        this.datasourceProvider = datasourceProvider;
    }

    @Override
    protected void handleFor(IntegrateInstrumentsCommand command) {
        final Datasource datasource = getDatasource(command.getDatasourceId());
        datasourceProvider.fetchInstruments(datasource).getUniqueValues().forEach(datasource::addInstrument);
        datasourceRepository.save(datasource);
    }
}
