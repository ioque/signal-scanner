package ru.ioque.investfund.application.command.handlers.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.command.CommandHandler;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerHandler extends CommandHandler<UpdateScannerCommand> {
    ScannerRepository scannerRepository;
    DatasourceRepository datasourceRepository;

    public UpdateScannerHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.dateTimeProvider = dateTimeProvider;
        this.scannerRepository = scannerRepository;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void handleFor(UpdateScannerCommand command) {
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        final Datasource datasource = datasourceRepository.getById(scanner.getDatasourceId());
        datasource.checkExistsTickers(command.getTickers());
        scanner.update(command);
        scannerRepository.save(scanner);
    }
}
