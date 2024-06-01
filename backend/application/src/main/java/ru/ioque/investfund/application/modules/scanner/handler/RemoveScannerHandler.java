package ru.ioque.investfund.application.modules.scanner.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.repository.SignalRepository;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.scanner.command.RemoveScanner;
import ru.ioque.investfund.domain.core.DomainException;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RemoveScannerHandler extends CommandHandler<RemoveScanner> {
    ScannerRepository scannerRepository;
    SignalRepository signalRepository;

    public RemoveScannerHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository,
        SignalRepository signalRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.scannerRepository = scannerRepository;
        this.signalRepository = signalRepository;
    }

    @Override
    protected Result businessProcess(RemoveScanner command) {
        if (!signalRepository.findAllBy(command.getScannerId()).isEmpty()) {
            return Result.error(new DomainException("Удаление сканера невозможно, есть зафиксированные сигналы."));
        }
        scannerRepository.removeBy(command.getScannerId());
        return Result.success();
    }
}
