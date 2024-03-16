package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.UUID;

/**
 * ПОТЕНЦИАЛЬНО ОТДЕЛЬНЫЙ СЕРВИС "СИГНАЛЫ", работа которого регулируется сервисом "РАСПИСАНИЕ"
 */
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerManager implements SystemModule {
    FinInstrumentRepository finInstrumentRepository;
    ScannerRepository scannerRepository;
    ScannerLogRepository scannerLogRepository;
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    LoggerFacade loggerFacade;

    public ScannerManager(
        FinInstrumentRepository finInstrumentRepository,
        ScannerRepository scannerRepository,
        ScannerLogRepository scannerLogRepository,
        UUIDProvider uuidProvider,
        DateTimeProvider dateTimeProvider,
        LoggerFacade loggerFacade
    ) {
        this.finInstrumentRepository = finInstrumentRepository;
        this.scannerRepository = scannerRepository;
        this.scannerLogRepository = scannerLogRepository;
        this.uuidProvider = uuidProvider;
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

    public synchronized void addNewScanner(AddScannerCommand command) {
        loggerFacade.logRunCreateSignalScanner(command);
        final UUID id = uuidProvider.generate();
        scannerRepository.save(
            command
                .getSignalConfig()
                .factoryScanner(
                    id,
                    finInstrumentRepository.getByIdIn(command.getSignalConfig().getObjectIds())
                )
        );
        loggerFacade.logSaveNewDataScanner(id);
    }

    public synchronized void updateScanner(UpdateScannerCommand command) {
        if (scannerRepository.getBy(command.getId()).isEmpty()) {
            throw new ApplicationException("Сканер сигналов с идентификатором " + command.getId() + " не найден.");
        }
        scannerRepository.save(
            command
                .getSignalConfig()
                .factoryScanner(
                    command.getId(),
                    finInstrumentRepository.getByIdIn(command.getSignalConfig().getObjectIds())
                )
        );
        loggerFacade.logUpdateSignalScanner(command);
    }

    private void runScanner(SignalScanner scanner) {
        loggerFacade.logRunWorkScanner(scanner);
        scannerLogRepository.saveAll(scanner.getId(), scanner.scanning(dateTimeProvider.nowDateTime()));
        scannerRepository.save(scanner);
        loggerFacade.logFinishWorkScanner(scanner);
    }
}
