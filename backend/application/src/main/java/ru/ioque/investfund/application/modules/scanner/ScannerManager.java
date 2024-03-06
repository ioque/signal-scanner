package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.DomainException;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;
import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * ПОТЕНЦИАЛЬНО ОТДЕЛЬНЫЙ СЕРВИС "СИГНАЛЫ", работа которого регулируется сервисом "РАСПИСАНИЕ"
 */
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerManager implements SystemModule {
    ScannerRepository scannerRepository;
    ScannerLogRepository scannerLogRepository;
    StatisticRepository statisticRepository;
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    LoggerFacade loggerFacade;

    public ScannerManager(
        ScannerRepository scannerRepository,
        ScannerLogRepository scannerLogRepository,
        StatisticRepository statisticRepository,
        UUIDProvider uuidProvider,
        DateTimeProvider dateTimeProvider,
        LoggerFacade loggerFacade
    ) {
        this.scannerRepository = scannerRepository;
        this.scannerLogRepository = scannerLogRepository;
        this.statisticRepository = statisticRepository;
        this.uuidProvider = uuidProvider;
        this.dateTimeProvider = dateTimeProvider;
        this.loggerFacade = loggerFacade;
    }

    @Override
    public void execute() {
        loggerFacade.logRunScanning(dateTimeProvider.nowDateTime());
        runScanners();
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

    private void runScanners() {
        scannerRepository
            .getAll()
            .stream()
            .filter(row -> row.isTimeForExecution(dateTimeProvider.nowDateTime()))
            .forEach(this::runScanner);
    }

    private Supplier<ApplicationException> scannerNotFound(UpdateScannerCommand command) {
        return () -> new ApplicationException("Сканер сигналов с идентификатором " + command.getId() + " не найден.");
    }

    private void runScanner(SignalScannerBot scanner) {
        loggerFacade.logRunWorkScanner(scanner);
        scannerLogRepository
            .saveAll(
                scanner.getId(),
                scanner.scanning(
                    getStatistics(scanner),
                    dateTimeProvider.nowDateTime()
                )
            );
        scannerRepository.save(scanner);
        loggerFacade.logFinishWorkScanner(scanner);
    }

    private List<InstrumentStatistic> getStatistics(SignalScannerBot scanner) {
        List<InstrumentStatistic> statistics = scanner
            .getObjectIds()
            .stream()
            .map(statisticRepository::getBy)
            .filter(Objects::nonNull)
            .toList();
        if (statistics.isEmpty()) {
            throw new DomainException("Нет статистических данных для выбранных инструментов.");
        }
        return scanner.getObjectIds().stream().map(statisticRepository::getBy).toList();
    }
}
