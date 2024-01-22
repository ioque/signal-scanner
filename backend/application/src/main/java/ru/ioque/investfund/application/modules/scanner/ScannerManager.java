package ru.ioque.investfund.application.modules.scanner;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ReportRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.exchange.value.statistic.InstrumentStatistic;
import ru.ioque.investfund.domain.scanner.financial.entity.Report;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * ПОТЕНЦИАЛЬНО ОТДЕЛЬНЫЙ СЕРВИС "СИГНАЛЫ", работа которого регулируется сервисом "РАСПИСАНИЕ"
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerManager {
    ScannerRepository scannerRepository;
    ReportRepository reportRepository;
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    LoggerFacade loggerFacade;

    public void scanning(List<InstrumentStatistic> statistics) {
        loggerFacade.logRunScanning(dateTimeProvider.nowDateTime());
        reportRepository.save(runScanners(statistics));
        loggerFacade.logFinishedScanning(dateTimeProvider.nowDateTime());
    }

    public void addNewScanner(AddScannerCommand command) {
        loggerFacade.logRunCreateSignalScanner(command);
        final SignalScannerBot bot = new SignalScannerBot(
            uuidProvider.generate(),
            command.getDescription(),
            command.getIds(),
            command.getSignalConfig()
        );
        scannerRepository.save(bot);
        loggerFacade.logSaveNewDataScanner(bot.getId());
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
                scannerBot.getConfig()
            )
        );
        loggerFacade.logUpdateSignalScanner(command);
    }

    private List<Report> runScanners(List<InstrumentStatistic> statistics) {
        List<Report> reports = new ArrayList<>();
        scannerRepository
            .getAll()
            .stream()
            .filter(row ->
                reportRepository
                    .getLastReportBy(row.getId())
                    .map(report ->
                        row.isTimeForExecution(
                            report.getTime(),
                            dateTimeProvider.nowDateTime()
                        )
                    )
                    .orElse(true)
            )
            .forEach(scanner -> runScanner(statistics, scanner, reports));
        return reports;
    }

    private Supplier<ApplicationException> scannerNotFound(UpdateScannerCommand command) {
        return () -> new ApplicationException("Сканер сигналов с идентификатором " + command.getId() + " не найден.");
    }

    private void runScanner(List<InstrumentStatistic> statistics, SignalScannerBot scanner, List<Report> reports) {
        loggerFacade.logRunWorkScanner(scanner);
        Report report = scanner.scanning(statistics
            .stream()
            .filter(row -> scanner.getObjectIds().contains(row.getInstrumentId()))
            .toList(), dateTimeProvider.nowDateTime());
        reports.add(report);
        loggerFacade.logFinishWorkScanner(scanner, report);
    }
}
