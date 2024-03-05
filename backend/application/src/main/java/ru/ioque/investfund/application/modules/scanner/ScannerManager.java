package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * ПОТЕНЦИАЛЬНО ОТДЕЛЬНЫЙ СЕРВИС "СИГНАЛЫ", работа которого регулируется сервисом "РАСПИСАНИЕ"
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerManager {
    ScannerRepository scannerRepository;
    ScannerLogRepository scannerLogRepository;
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    LoggerFacade loggerFacade;

    public void scanning(List<InstrumentStatistic> statistics) {
        loggerFacade.logRunScanning(dateTimeProvider.nowDateTime());
        runScanners(statistics);
        loggerFacade.logFinishedScanning(dateTimeProvider.nowDateTime());
    }

    public void addNewScanner(AddScannerCommand command) {
        loggerFacade.logRunCreateSignalScanner(command);
        final UUID id = uuidProvider.generate();
        scannerRepository.save(SignalScannerBot.builder()
            .id(id)
            .description(command.getDescription())
            .objectIds(command.getIds())
            .config(command.getSignalConfig())
            .build());
        loggerFacade.logSaveNewDataScanner(id);
    }

    public void updateScanner(UpdateScannerCommand command) {
        final SignalScannerBot scannerBot =
            scannerRepository
                .getBy(command.getId())
                .orElseThrow(scannerNotFound(command));
        scannerRepository.save(
            new SignalScannerBot(
                scannerBot.getId(),
                command.getDescription(),
                command.getIds(),
                scannerBot.getConfig(),
                scannerBot.getLastExecutionDateTime().orElse(null),
                scannerBot.getSignals()
            )
        );
        loggerFacade.logUpdateSignalScanner(command);
    }

    private void runScanners(List<InstrumentStatistic> statistics) {
        scannerRepository
            .getAll()
            .stream()
            .filter(row -> row.isTimeForExecution(dateTimeProvider.nowDateTime()))
            .forEach(scanner -> runScanner(statistics, scanner));
    }

    private Supplier<ApplicationException> scannerNotFound(UpdateScannerCommand command) {
        return () -> new ApplicationException("Сканер сигналов с идентификатором " + command.getId() + " не найден.");
    }

    private void runScanner(List<InstrumentStatistic> statistics, SignalScannerBot scanner) {
        loggerFacade.logRunWorkScanner(scanner);
        scannerLogRepository
            .saveAll(
                scanner.getId(),
                scanner.scanning(
                    statistics
                        .stream()
                        .filter(row -> scanner.getObjectIds().contains(row.getInstrumentId()))
                        .toList(), dateTimeProvider.nowDateTime()
                )
            );
        scannerRepository.save(scanner);
        loggerFacade.logFinishWorkScanner(scanner);
    }
}
