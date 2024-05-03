package ru.ioque.investfund.application.modules.risk.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.application.adapters.InstrumentRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
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
    EmulatedPositionRepository emulatedPositionRepository;
    InstrumentRepository instrumentRepository;
    ScannerRepository scannerRepository;

    public OpenEmulatedPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        EmulatedPositionRepository emulatedPositionRepository,
        InstrumentRepository instrumentRepository,
        ScannerRepository scannerRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.emulatedPositionRepository = emulatedPositionRepository;
        this.instrumentRepository = instrumentRepository;
        this.scannerRepository = scannerRepository;
    }

    @Override
    protected Result businessProcess(OpenEmulatedPosition command) {
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        final Instrument instrument = instrumentRepository.getBy(command.getInstrumentId());
        if (emulatedPositionRepository.findBy(command.getInstrumentId(), command.getScannerId()).isPresent()) {
            return Result.error(
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
            .id(emulatedPositionRepository.nextId())
            .isOpen(true)
            .scanner(scanner)
            .instrument(instrument)
            .openPrice(command.getPrice())
            .build();
        emulatedPosition.updateLastPrice(command.getPrice());
        emulatedPositionRepository.save(emulatedPosition);
        return Result.success();
    }
}
