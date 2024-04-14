package ru.ioque.investfund.application.modules.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerLogRepository;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateScannerProcessor extends CommandProcessor<CreateScannerCommand> {
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    ScannerRepository scannerRepository;
    ScannerLogRepository scannerLogRepository;
    DatasourceRepository datasourceRepository;

    public CreateScannerProcessor(
        Validator validator,
        LoggerFacade loggerFacade,
        UUIDProvider uuidProvider,
        DateTimeProvider dateTimeProvider,
        ScannerRepository scannerRepository,
        ScannerLogRepository scannerLogRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(validator, loggerFacade);
        this.uuidProvider = uuidProvider;
        this.dateTimeProvider = dateTimeProvider;
        this.datasourceRepository = datasourceRepository;
        this.scannerLogRepository = scannerLogRepository;
        this.scannerRepository = scannerRepository;
    }

    @Override
    protected void handleFor(CreateScannerCommand command) {
        SignalScanner scanner = SignalScanner.from(uuidProvider.generate(), command);
        datasourceRepository
            .getBy(command.getDatasourceId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Источник данных[id=%s] не существует.", command.getDatasourceId())
                )
            ).checkExistsTickers(command.getTickers());
        scannerRepository.save(scanner);
        scannerLogRepository.save(
            scanner.getId(),
            new ScannerLog(
                dateTimeProvider.nowDateTime(),
                "Создан сканер сигналов, параметры: " + command.getProperties().prettyPrint()
            )
        );
    }
}
