package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerManager implements SystemModule {
    ScannerRepository scannerRepository;
    ScannerLogRepository scannerLogRepository;
    DateTimeProvider dateTimeProvider;
    LoggerFacade loggerFacade;

    public ScannerManager(
        ScannerRepository scannerRepository,
        ScannerLogRepository scannerLogRepository,
        DateTimeProvider dateTimeProvider,
        LoggerFacade loggerFacade
    ) {
        this.scannerRepository = scannerRepository;
        this.scannerLogRepository = scannerLogRepository;
        this.dateTimeProvider = dateTimeProvider;
        this.loggerFacade = loggerFacade;
    }

    @Override
    public synchronized void execute() {
        loggerFacade.logRunScanning(dateTimeProvider.nowDateTime());
        scannerRepository
            .getAll()
            .stream()
            .filter(row -> row.isTimeForExecution(dateTimeProvider.nowDateTime()))
            .forEach(this::runScanner);
        loggerFacade.logFinishedScanning(dateTimeProvider.nowDateTime());
    }

    private void runScanner(SignalScanner scanner) {
        loggerFacade.logRunWorkScanner(scanner);
        scannerLogRepository.saveAll(scanner.getId(), scanner.scanning(dateTimeProvider.nowDateTime()));
        scannerRepository.save(scanner);
        loggerFacade.logFinishWorkScanner(scanner);
    }
}
