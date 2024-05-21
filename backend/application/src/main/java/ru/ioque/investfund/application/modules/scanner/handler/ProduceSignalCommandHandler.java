package ru.ioque.investfund.application.modules.scanner.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventJournal;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.application.integration.event.DatasourceScanned;
import ru.ioque.investfund.application.integration.event.SignalRegistered;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.scanner.command.ProduceSignal;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.WorkScannerReport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProduceSignalCommandHandler extends CommandHandler<ProduceSignal> {
    ScannerRepository scannerRepository;
    TradingSnapshotsRepository snapshotsRepository;
    EventJournal eventJournal;

    public ProduceSignalCommandHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository,
        TradingSnapshotsRepository snapshotsRepository,
        EventJournal eventJournal
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.scannerRepository = scannerRepository;
        this.snapshotsRepository = snapshotsRepository;
        this.eventJournal = eventJournal;
    }

    @Override
    protected Result businessProcess(ProduceSignal command) {
        final List<ApplicationLog> logs = scannerRepository
            .findAllBy(command.getDatasourceId())
            .stream()
            .filter(scanner -> scanner.isTimeForExecution(command.getWatermark()) && scanner.isActive())
            .map(scanner -> runScanner(scanner, command.getWatermark()))
            .flatMap(Collection::stream)
            .toList();
        eventJournal.publish(DatasourceScanned.builder()
            .datasourceId(command.getDatasourceId().getUuid())
            .watermark(command.getWatermark())
            .createdAt(dateTimeProvider.nowDateTime())
            .build());
        return Result.success(logs);
    }

    private List<ApplicationLog> runScanner(SignalScanner scanner, LocalDateTime watermark) {
        final List<ApplicationLog> logs = new ArrayList<>();
        final List<TradingSnapshot> snapshots = snapshotsRepository.findAllBy(scanner.getInstrumentIds());
        snapshots.forEach(snapshot -> logs.add(
            new InfoLog(
                dateTimeProvider.nowDateTime(),
                snapshot.toString()
            )
        ));
        logs.add(
            new InfoLog(dateTimeProvider.nowDateTime(), String.format(
                "Начал работу сканер[id=%s], алгоритм %s.",
                scanner.getId(),
                scanner.getProperties().getType().getName()
            ))
        );
        final WorkScannerReport report = scanner.scanning(snapshots, watermark);
        logs.add(
            new InfoLog(dateTimeProvider.nowDateTime(), String.format(
                "Завершил работу сканер[id=%s], алгоритм %s. Найдены сигналы: %s, из них зарегистрированы: %s",
                scanner.getId(),
                scanner.getProperties().getType().getName(),
                report.getFoundedSignals(),
                report.getRegisteredSignals()
            ))
        );
        scannerRepository.save(scanner);
        report.getRegisteredSignals()
            .stream()
            .map(signal -> SignalRegistered.builder()
                .price(signal.getPrice())
                .scannerId(scanner.getId().getUuid())
                .instrumentId(signal.getInstrumentId().getUuid())
                .isBuy(signal.isBuy())
                .createdAt(dateTimeProvider.nowDateTime())
                .build()
            )
            .forEach(eventJournal::publish);
        return logs;
    }
}
