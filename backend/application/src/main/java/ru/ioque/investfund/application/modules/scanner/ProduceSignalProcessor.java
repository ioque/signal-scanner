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

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProduceSignalProcessor extends CommandProcessor<ProduceSignalCommand> {
    ScannerRepository scannerRepository;
    TradingDataRepository tradingDataRepository;
    EventPublisher eventPublisher;

    public ProduceSignalProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerFacade loggerFacade,
        ScannerRepository scannerRepository,
        TradingDataRepository tradingDataRepository,
        EventPublisher eventPublisher
    ) {
        super(dateTimeProvider, validator, loggerFacade);
        this.dateTimeProvider = dateTimeProvider;
        this.scannerRepository = scannerRepository;
        this.tradingDataRepository = tradingDataRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void handleFor(ProduceSignalCommand command) {
        executeBusinessProcess(
            () -> {
                scannerRepository
                    .getAllBy(command.getDatasourceId())
                    .stream()
                    .filter(scanner -> scanner.isTimeForExecution(command.getWatermark()))
                    .forEach(scanner -> {
                        final List<Signal> newSignals = scanner.scanning(
                            tradingDataRepository.findBy(scanner.getDatasourceId(), scanner.getTickers()),
                            command.getWatermark()
                        );
                        scannerRepository.save(scanner);
                        newSignals.forEach(signal -> eventPublisher.publish(SignalEvent.from(signal)));
                    });
            },
            String.format(
                "Выполнен поиск торговых сигналов в источнике данных[id=%s], время потока %s",
                command.getDatasourceId(),
                command.getWatermark()
            )
        );
    }
}
