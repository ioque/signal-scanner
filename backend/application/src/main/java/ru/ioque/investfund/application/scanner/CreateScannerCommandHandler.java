package ru.ioque.investfund.application.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.CommandHandler;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

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
        final Datasource datasource = datasourceRepository.getById(command.getDatasourceId());
        datasource.checkExistsInstrument(command.getInstrumentIds());
        final SignalScanner scanner = SignalScanner.of(ScannerId.from(uuidProvider.generate()), command);
        scannerRepository.save(scanner);
    }
}
