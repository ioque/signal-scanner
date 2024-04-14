package ru.ioque.investfund.application.modules.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TradingDataRepository;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.event.SignalEvent;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProduceSignalProcessor extends CommandProcessor<ProduceSignalCommand> {
    DateTimeProvider dateTimeProvider;
    ScannerRepository scannerRepository;
    TradingDataRepository tradingDataRepository;
    EventPublisher eventPublisher;

    public ProduceSignalProcessor(
        Validator validator,
        LoggerFacade loggerFacade,
        DateTimeProvider dateTimeProvider,
        ScannerRepository scannerRepository,
        TradingDataRepository tradingDataRepository,
        EventPublisher eventPublisher
    ) {
        super(validator, loggerFacade);
        this.dateTimeProvider = dateTimeProvider;
        this.scannerRepository = scannerRepository;
        this.tradingDataRepository = tradingDataRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void handleFor(ProduceSignalCommand command) {
        loggerFacade.logRunScanning(dateTimeProvider.nowDateTime());
        scannerRepository
            .getAllBy(command.getDatasourceId())
            .stream()
            .filter(scanner -> scanner.isTimeForExecution(command.getWatermark()))
            .forEach(scanner -> {
                loggerFacade.logRunWorkScanner(scanner);
                final List<TradingSnapshot> snapshots = tradingDataRepository.findBy(
                    scanner.getDatasourceId(),
                    scanner.getTickers()
                );
                final List<Signal> newSignals = scanner.scanning(
                    snapshots,
                    command.getWatermark()
                );
                scannerRepository.save(scanner);
                newSignals.forEach(signal -> {
                    eventPublisher.publish(SignalEvent.from(signal));
                });
                loggerFacade.logFinishWorkScanner(scanner);
            });
        loggerFacade.logFinishedScanning(dateTimeProvider.nowDateTime());
    }
}
