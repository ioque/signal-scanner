package ru.ioque.investfund.application.modules.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerProcessor extends CommandProcessor<UpdateScannerCommand> {
    DateTimeProvider dateTimeProvider;
    ScannerRepository scannerRepository;
    ScannerLogRepository scannerLogRepository;
    DatasourceRepository datasourceRepository;

    public UpdateScannerProcessor(
        Validator validator,
        LoggerFacade loggerFacade,
        DateTimeProvider dateTimeProvider,
        ScannerRepository scannerRepository,
        ScannerLogRepository scannerLogRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(validator, loggerFacade);
        this.dateTimeProvider = dateTimeProvider;
        this.scannerRepository = scannerRepository;
        this.scannerLogRepository = scannerLogRepository;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void handleFor(UpdateScannerCommand command) {
        SignalScanner scanner = scannerRepository
            .getBy(command.getScannerId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Сканер[id=%s] не существует.", command.getScannerId())
                )
            );
        datasourceRepository
            .getBy(scanner.getDatasourceId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", scanner.getDatasourceId())
                )
            ).checkExistsTickers(command.getTickers());

        scanner.update(command);

        scannerRepository.save(scanner);
        scannerLogRepository.save(
            scanner.getId(),
            new ScannerLog(
                dateTimeProvider.nowDateTime(),
                "Обновлен сканер сигналов, параметры: " + command.getProperties().prettyPrint()
            )
        );
    }
}
