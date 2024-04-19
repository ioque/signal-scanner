package ru.ioque.investfund.application.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.CommandHandler;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.event.ScanningFinishedEvent;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProduceSignalCommandHandler extends CommandHandler<ProduceSignalCommand> {
    ScannerRepository scannerRepository;
    TradingSnapshotsRepository snapshotsRepository;
    EventPublisher eventPublisher;

    public ProduceSignalCommandHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository,
        TradingSnapshotsRepository snapshotsRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.scannerRepository = scannerRepository;
        this.snapshotsRepository = snapshotsRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void businessProcess(ProduceSignalCommand command) {
        scannerRepository
            .findAllBy(command.getDatasourceId())
            .stream()
            .filter(scanner -> scanner.isTimeForExecution(command.getWatermark()))
            .forEach(scanner -> runScanner(scanner, command.getWatermark()));
        eventPublisher.publish(
            ScanningFinishedEvent.builder()
                .datasourceId(command.getDatasourceId())
                .watermark(command.getWatermark())
                .dateTime(dateTimeProvider.nowDateTime())
                .build()
        );
    }

    private void runScanner(SignalScanner scanner, LocalDateTime watermark) {
        final List<TradingSnapshot> snapshots = snapshotsRepository.findAllBy(scanner.getDatasourceId(), scanner.getInstrumentIds());
        scanner.scanning(snapshots, watermark);
        scannerRepository.save(scanner);
        scanner.getEvents().forEach(eventPublisher::publish);
    }
}
