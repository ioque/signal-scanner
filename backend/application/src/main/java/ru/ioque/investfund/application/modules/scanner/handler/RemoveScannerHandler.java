package ru.ioque.investfund.application.modules.scanner.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.scanner.command.RemoveScanner;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RemoveScannerHandler extends CommandHandler<RemoveScanner> {
    ScannerRepository scannerRepository;
    EmulatedPositionJournal emulatedPositionJournal;

    public RemoveScannerHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository,
        EmulatedPositionJournal emulatedPositionJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.scannerRepository = scannerRepository;
        this.emulatedPositionJournal = emulatedPositionJournal;
    }

    @Override
    protected Result businessProcess(RemoveScanner command) {
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        for (InstrumentId instrumentId : scanner.getInstrumentIds()) {
            if (emulatedPositionJournal.findActualBy(instrumentId, command.getScannerId()).isPresent()) {
                return Result.error(new DomainException("Удаление сканера невозможно, есть эмуляции позиций."));
            }
        }
        scannerRepository.removeBy(command.getScannerId());
        return Result.success();
    }
}
