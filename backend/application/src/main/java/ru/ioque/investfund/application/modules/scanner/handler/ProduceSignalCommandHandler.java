package ru.ioque.investfund.application.modules.scanner.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.application.integration.event.DatasourceScanned;
import ru.ioque.investfund.application.integration.event.SignalRegistered;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    protected Result businessProcess(ProduceSignalCommand command) {
        final List<ApplicationLog> logs = scannerRepository
            .findAllBy(command.getDatasourceId())
            .stream()
            .filter(scanner -> scanner.isTimeForExecution(command.getWatermark()))
            .map(scanner -> runScanner(scanner, command.getWatermark()))
            .flatMap(Collection::stream)
            .toList();
        eventPublisher.publish(DatasourceScanned.builder()
            .datasourceId(command.getDatasourceId().getUuid())
            .watermark(command.getWatermark())
            .createdAt(dateTimeProvider.nowDateTime())
            .build());
        return Result.success(logs);
    }

    private List<ApplicationLog> runScanner(SignalScanner scanner, LocalDateTime watermark) {
        final List<TradingSnapshot> snapshots = snapshotsRepository.findAllBy(scanner.getInstrumentIds());
        final List<Signal> signals = scanner.scanning(snapshots, watermark);
        final Set<String> logs = scanner.getLogs();
        scannerRepository.save(scanner);
        signals
            .stream()
            .map(signal -> SignalRegistered.builder()
                .price(signal.getPrice())
                .scannerId(scanner.getId().getUuid())
                .instrumentId(signal.getInstrumentId().getUuid())
                .isBuy(signal.isBuy())
                .createdAt(dateTimeProvider.nowDateTime())
                .build()
            )
            .forEach(eventPublisher::publish);
        return logs
            .stream()
            .map(log -> (ApplicationLog) new InfoLog(dateTimeProvider.nowDateTime(), log.replaceAll("\\n", " ")))
            .toList();
    }
}
