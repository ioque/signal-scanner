package ru.ioque.investfund.application.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.CommandHandler;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateScannerCommandHandler extends CommandHandler<CreateScannerCommand> {
    UUIDProvider uuidProvider;
    ScannerRepository scannerRepository;
    DatasourceRepository datasourceRepository;

    public CreateScannerCommandHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        ScannerRepository scannerRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.dateTimeProvider = dateTimeProvider;
        this.datasourceRepository = datasourceRepository;
        this.scannerRepository = scannerRepository;
    }

    @Override
    protected void businessProcess(CreateScannerCommand command) {
        final Datasource datasource = datasourceRepository.getBy(command.getDatasourceId());
        final List<InstrumentId> instrumentIds = datasource.findInstrumentIds(command.getTickers());
        final SignalScanner scanner = SignalScanner.builder()
            .id(ScannerId.from(uuidProvider.generate()))
            .workPeriodInMinutes(command.getWorkPeriodInMinutes())
            .description(command.getDescription())
            .datasourceId(command.getDatasourceId())
            .instrumentIds(instrumentIds)
            .properties(command.getProperties())
            .signals(new ArrayList<>())
            .lastExecutionDateTime(null)
            .build();
        scannerRepository.save(scanner);
    }
}
