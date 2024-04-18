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
import ru.ioque.investfund.domain.scanner.event.ScanningFinishedEvent;
import ru.ioque.investfund.domain.scanner.event.SignalFoundEvent;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProduceSignalHandler extends CommandHandler<ProduceSignalCommand> {
    UUIDProvider uuidProvider;
    ScannerRepository scannerRepository;
    TradingSnapshotsRepository tradingSnapshotsRepository;
    EventPublisher eventPublisher;

    public ProduceSignalHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        ScannerRepository scannerRepository,
        TradingSnapshotsRepository tradingSnapshotsRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.scannerRepository = scannerRepository;
        this.tradingSnapshotsRepository = tradingSnapshotsRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void handleFor(ProduceSignalCommand command) {
        scannerRepository
            .findAllBy(command.getDatasourceId())
            .stream()
            .filter(scanner -> scanner.isTimeForExecution(command.getWatermark()))
            .forEach(scanner -> {
                final List<Signal> newSignals = scanner
                    .scanning(
                        tradingSnapshotsRepository.findAllBy(scanner.getDatasourceId(), scanner.getTickers()),
                        command.getWatermark()
                    )
                    .stream()
                    .map(signalSign -> Signal.of(uuidProvider.generate(), scanner.getId(), signalSign))
                    .toList();
                scanner.addNewSignals(newSignals);
                scannerRepository.save(scanner);
                newSignals.forEach(signal -> eventPublisher.publish(
                    SignalFoundEvent.builder()
                        .isBuy(signal.isBuy())
                        .ticker(signal.getTicker())
                        .watermark(command.getWatermark())
                        .datasourceId(command.getDatasourceId())
                        .build()
                ));
            });
        eventPublisher.publish(
            ScanningFinishedEvent.builder()
                .datasourceId(command.getDatasourceId())
                .watermark(command.getWatermark())
                .dateTime(dateTimeProvider.nowDateTime())
                .build()
        );
    }
}
