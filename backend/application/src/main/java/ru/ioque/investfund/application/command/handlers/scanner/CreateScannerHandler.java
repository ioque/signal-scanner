package ru.ioque.investfund.application.command.handlers.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.command.CommandHandler;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateScannerHandler extends CommandHandler<CreateScannerCommand> {
    UUIDProvider uuidProvider;
    ScannerRepository scannerRepository;
    DatasourceRepository datasourceRepository;

    public CreateScannerHandler(
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
    protected void handleFor(CreateScannerCommand command) {
        final Datasource datasource = datasourceRepository.getById(command.getDatasourceId());
        datasource.checkExistsInstrument(command.getInstrumentIds());
        final UUID newScannerId = uuidProvider.generate();
        final SignalScanner scanner = SignalScanner.of(newScannerId, command);
        scannerRepository.save(scanner);
    }
}
