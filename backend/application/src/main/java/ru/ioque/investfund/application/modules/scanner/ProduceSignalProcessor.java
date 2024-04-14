package ru.ioque.investfund.application.modules.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TradingDataRepository;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.scanner.command.ProduceSignalCommand;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProduceSignalProcessor extends CommandProcessor<ProduceSignalCommand> {
    DateTimeProvider dateTimeProvider;
    ScannerRepository scannerRepository;
    ScannerLogRepository scannerLogRepository;
    TradingDataRepository tradingDataRepository;

    public ProduceSignalProcessor(
        Validator validator,
        LoggerFacade loggerFacade,
        DateTimeProvider dateTimeProvider,
        ScannerRepository scannerRepository,
        ScannerLogRepository scannerLogRepository,
        TradingDataRepository tradingDataRepository
    ) {
        super(validator, loggerFacade);
        this.dateTimeProvider = dateTimeProvider;
        this.scannerRepository = scannerRepository;
        this.scannerLogRepository = scannerLogRepository;
        this.tradingDataRepository = tradingDataRepository;
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
                final ScannerLog log = scanner.scanning(
                    snapshots,
                    command.getWatermark()
                );
                scannerLogRepository.save(scanner.getId(), log);
                scannerRepository.save(scanner);
                loggerFacade.logFinishWorkScanner(scanner);
            });
        loggerFacade.logFinishedScanning(dateTimeProvider.nowDateTime());
    }
}
