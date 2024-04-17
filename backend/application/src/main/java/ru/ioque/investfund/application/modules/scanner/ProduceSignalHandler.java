package ru.ioque.investfund.application.modules.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TradingSnapshotsRepository;
import ru.ioque.investfund.application.modules.CommandHandler;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.event.SignalEvent;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProduceSignalHandler extends CommandHandler<ProduceSignalCommand> {
    ScannerRepository scannerRepository;
    TradingSnapshotsRepository tradingSnapshotsRepository;
    EventPublisher eventPublisher;

    public ProduceSignalHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository,
        TradingSnapshotsRepository tradingSnapshotsRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.dateTimeProvider = dateTimeProvider;
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
                final List<Signal> newSignals = scanner.scanning(
                    tradingSnapshotsRepository.findAllBy(scanner.getDatasourceId(), scanner.getTickers()),
                    command.getWatermark()
                );
                scannerRepository.save(scanner);
                newSignals.forEach(signal -> eventPublisher.publish(SignalEvent.from(signal)));
            });
    }
}
