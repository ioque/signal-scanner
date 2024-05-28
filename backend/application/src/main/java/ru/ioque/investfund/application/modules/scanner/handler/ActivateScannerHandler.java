package ru.ioque.investfund.application.modules.scanner.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.scanner.command.ActivateScanner;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ActivateScannerHandler extends CommandHandler<ActivateScanner> {
    ScannerRepository scannerRepository;

    public ActivateScannerHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.scannerRepository = scannerRepository;
    }

    @Override
    protected Result businessProcess(ActivateScanner command) {
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        scanner.activate();
        scannerRepository.save(scanner);
        return Result.success();
    }
}
