package ru.ioque.investfund.application.datasource;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.CommandHandler;
import ru.ioque.investfund.application.adapters.DatasourceProvider;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.command.IntegrateInstrumentsCommand;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntegrateInstrumentsHandler extends CommandHandler<IntegrateInstrumentsCommand> {
    UUIDProvider uuidProvider;
    DatasourceRepository datasourceRepository;
    DatasourceProvider datasourceProvider;

    public IntegrateInstrumentsHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        DatasourceProvider datasourceProvider,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.datasourceProvider = datasourceProvider;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void businessProcess(IntegrateInstrumentsCommand command) {
        final Datasource datasource = datasourceRepository.getById(command.getDatasourceId());
        final List<InstrumentDetails> detailsList = datasourceProvider.fetchInstrumentDetails(
            datasource
        ).getInstrumentDetails(validator);
        final List<Instrument> instruments = detailsList
            .stream()
            .map(details -> Instrument.of(InstrumentId.from(uuidProvider.generate()), details))
            .toList();
        datasource.integrateInstruments(instruments);
        datasourceRepository.save(datasource);
    }
}
