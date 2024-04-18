package ru.ioque.investfund.application.command.handlers.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.command.CommandHandler;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.event.ScanningFinishedEvent;
import ru.ioque.investfund.domain.scanner.event.SignalFoundEvent;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProduceSignalHandler extends CommandHandler<ProduceSignalCommand> {
    UUIDProvider uuidProvider;
    ScannerRepository scannerRepository;
    TradingSnapshotsRepository snapshotsRepository;
    EventPublisher eventPublisher;

    public ProduceSignalHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        ScannerRepository scannerRepository,
        TradingSnapshotsRepository snapshotsRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.scannerRepository = scannerRepository;
        this.snapshotsRepository = snapshotsRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void handleFor(ProduceSignalCommand command) {
        scannerRepository
            .findAllBy(command.getDatasourceId())
            .stream()
            .filter(scanner -> scanner.isTimeForExecution(command.getWatermark()))
            .forEach(scanner -> runScanner(scanner, command.getWatermark()));
        eventPublisher.publish(
            ScanningFinishedEvent.builder()
                .id(uuidProvider.generate())
                .datasourceId(command.getDatasourceId())
                .watermark(command.getWatermark())
                .dateTime(dateTimeProvider.nowDateTime())
                .build()
        );
    }

    private void runScanner(SignalScanner scanner, LocalDateTime watermark) {
        final List<TradingSnapshot> snapshots = snapshotsRepository.findAllBy(scanner.getDatasourceId(), scanner.getTickers());
        final List<Signal> newSignals = scanner.scanning(snapshots, watermark);
        scannerRepository.save(scanner);
        newSignals.forEach(signal -> eventPublisher.publish(
            SignalFoundEvent.builder()
                .id(uuidProvider.generate())
                .watermark(watermark)
                .isBuy(signal.isBuy())
                .scannerId(scanner.getId())
                .ticker(signal.getTicker())
                .build()
        ));
    }
}
