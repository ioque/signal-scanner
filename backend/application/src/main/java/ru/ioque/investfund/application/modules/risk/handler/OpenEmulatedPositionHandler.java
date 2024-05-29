package ru.ioque.investfund.application.modules.risk.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.journal.EmulatedPositionJournal;
import ru.ioque.investfund.application.adapters.repository.InstrumentRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.domain.core.WarningLog;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OpenEmulatedPositionHandler extends CommandHandler<OpenEmulatedPosition> {
    EmulatedPositionJournal emulatedPositionJournal;
    InstrumentRepository instrumentRepository;
    ScannerRepository scannerRepository;

    public OpenEmulatedPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        EmulatedPositionJournal emulatedPositionJournal,
        InstrumentRepository instrumentRepository,
        ScannerRepository scannerRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.emulatedPositionJournal = emulatedPositionJournal;
        this.instrumentRepository = instrumentRepository;
        this.scannerRepository = scannerRepository;
    }

    @Override
    protected Result businessProcess(OpenEmulatedPosition command) {
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        final Instrument instrument = instrumentRepository.getBy(command.getInstrumentId());
        if (emulatedPositionJournal.findActualBy(command.getInstrumentId(), command.getScannerId()).isPresent()) {
            return Result.success(
                List.of(
                    new WarningLog(
                        dateTimeProvider.nowDateTime(),
                        String.format(
                            "Эмуляция позиции[scannerId=%s, ticker=%s] уже открыта.",
                            scanner.getId(),
                            instrument.getTicker()
                        )
                    )
                )
            );
        }
        final EmulatedPosition emulatedPosition = EmulatedPosition.builder()
            .id(emulatedPositionJournal.nextId())
            .isOpen(true)
            .scanner(scanner)
            .instrument(instrument)
            .openPrice(command.getPrice())
            .build();
        emulatedPosition.updateLastPrice(command.getPrice());
        emulatedPositionJournal.publish(emulatedPosition);
        return Result.success();
    }
}
