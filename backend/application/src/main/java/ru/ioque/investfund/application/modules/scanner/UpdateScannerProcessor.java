package ru.ioque.investfund.application.modules.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerProcessor extends CommandProcessor<UpdateScannerCommand> {
    ScannerRepository scannerRepository;
    DatasourceRepository datasourceRepository;

    public UpdateScannerProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerFacade loggerFacade,
        ScannerRepository scannerRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerFacade);
        this.dateTimeProvider = dateTimeProvider;
        this.scannerRepository = scannerRepository;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected void handleFor(UpdateScannerCommand command) {
        final SignalScanner scanner = getScanner(command.getScannerId());
        final Datasource datasource = getDatasource(scanner.getDatasourceId());
        datasource.checkExistsTickers(command.getTickers());
        executeBusinessProcess(
            () -> {
                scanner.update(command);
                scannerRepository.save(scanner);
            },
            String.format("Сканер[id=%s] обновлен", scanner.getId())
        );
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

    private SignalScanner getScanner(UUID scannerId) {
        return scannerRepository
            .getBy(scannerId)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Сканер[id=%s] не существует.", scannerId)
                )
            );
    }
}
