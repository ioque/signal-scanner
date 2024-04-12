package ru.ioque.investfund.application.modules.scanner;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TradingDataRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.core.Command;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.command.ScanningCommand;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerManager {
    Validator validator;
    DatasourceRepository datasourceRepository;
    TradingDataRepository tradingDataRepository;
    ScannerRepository scannerRepository;
    ScannerLogRepository scannerLogRepository;
    DateTimeProvider dateTimeProvider;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;

    public ScannerManager(
        Validator validator,
        DatasourceRepository datasourceRepository,
        TradingDataRepository tradingDataRepository,
        ScannerRepository scannerRepository,
        ScannerLogRepository scannerLogRepository,
        DateTimeProvider dateTimeProvider,
        UUIDProvider uuidProvider,
        LoggerFacade loggerFacade
    ) {
        this.validator = validator;
        this.datasourceRepository = datasourceRepository;
        this.tradingDataRepository = tradingDataRepository;
        this.scannerRepository = scannerRepository;
        this.scannerLogRepository = scannerLogRepository;
        this.dateTimeProvider = dateTimeProvider;
        this.uuidProvider = uuidProvider;
        this.loggerFacade = loggerFacade;
    }

    public synchronized void scanning(ScanningCommand command) {
        validateCommand(command);
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
                final List<ScannerLog> logs = scanner.scanning(
                    snapshots,
                    command.getWatermark()
                );
                scannerLogRepository.saveAll(scanner.getId(), logs);
                scannerRepository.save(scanner);
                loggerFacade.logFinishWorkScanner(scanner);
            });
        loggerFacade.logFinishedScanning(dateTimeProvider.nowDateTime());
    }

    public synchronized void createScanner(CreateScannerCommand command) {
        validateCommand(command);
        validateDatasource(command.getDatasourceId(), command.getTickers());

        SignalScanner scanner = SignalScanner.from(uuidProvider.generate(), command);

        scannerRepository.save(scanner);
        scannerLogRepository.saveAll(
            scanner.getId(),
            List.of(new ScannerLog(
                "Создан сканер сигналов, параметры: " + command.getProperties().prettyPrint(),
                dateTimeProvider.nowDateTime()
            ))
        );
    }

    public synchronized void updateScanner(UpdateScannerCommand command) {
        validateCommand(command);
        SignalScanner scanner = getScanner(command.getScannerId());
        validateDatasource(scanner.getDatasourceId(), command.getTickers());

        scanner.update(command);

        scannerRepository.save(scanner);
        scannerLogRepository.saveAll(
            scanner.getId(),
            List.of(new ScannerLog(
                "Обновлен сканер сигналов, параметры: " + command.getProperties().prettyPrint(),
                dateTimeProvider.nowDateTime()
            ))
        );
    }

    private void validateCommand(Command command) {
        Set<ConstraintViolation<Command>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private SignalScanner getScanner(UUID scannerId) {
        return scannerRepository
            .getBy(scannerId)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Сканер[id=%s] не существует.", scannerId)
                )
            );
    }

    private void validateDatasource(
        UUID scanner,
        List<String> tickers
    ) {
        Datasource datasource = getDatasource(scanner);
        List<String> notExistedTickers = tickers
            .stream()
            .filter(ticker -> !datasource.getTickers().contains(ticker))
            .toList();
        if (!notExistedTickers.isEmpty()) {
            throw new IllegalArgumentException(
                String
                    .format(
                        "В выбранном источнике данных не существует инструментов с тикерами %s.",
                        notExistedTickers
                    )
            );
        }
    }

    private Datasource getDatasource(UUID datasourceId) {
        return datasourceRepository
            .getBy(datasourceId)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", datasourceId)
                )
            );
    }
}
