package ru.ioque.investfund.application.risk;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EmulatedPositionRepository;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.risk.command.OpenEmulatedPosition;
import ru.ioque.investfund.domain.core.EntityNotFoundException;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.risk.EmulatedPosition;
import ru.ioque.investfund.domain.risk.EmulatedPositionId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OpenEmulatedPositionHandler extends CommandHandler<OpenEmulatedPosition> {
    EmulatedPositionRepository emulatedPositionRepository;
    ScannerRepository scannerRepository;

    public OpenEmulatedPositionHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        EmulatedPositionRepository emulatedPositionRepository,
        ScannerRepository scannerRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider, uuidProvider);
        this.emulatedPositionRepository = emulatedPositionRepository;
        this.scannerRepository = scannerRepository;
    }

    @Override
    protected void businessProcess(OpenEmulatedPosition command) {
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        if (!scanner.getInstrumentIds().contains(command.getInstrumentId())) {
            throw new EntityNotFoundException(String.format("Инструмент[id=%s] не существует.", command.getInstrumentId()));
        }
        if (emulatedPositionRepository.findBy(command.getInstrumentId(), command.getScannerId()).isPresent()) {
            return;
        }
        EmulatedPosition emulatedPosition = EmulatedPosition.builder()
            .id(EmulatedPositionId.from(uuidProvider.generate()))
            .isOpen(true)
            .openPrice(command.getPrice())
            .instrumentId(command.getInstrumentId())
            .scannerId(command.getScannerId())
            .build();
        emulatedPosition.updateLastPrice(command.getPrice());
        emulatedPositionRepository.save(emulatedPosition);
        loggerProvider.log(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Открыта эмуляция позиции[id=%s]", emulatedPosition.getId()),
            command.getTrack()
        ));
    }
}
