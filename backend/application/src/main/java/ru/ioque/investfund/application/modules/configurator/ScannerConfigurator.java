package ru.ioque.investfund.application.modules.configurator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.ScannerConfigRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.configurator.command.SaveScannerCommand;
import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;
import ru.ioque.investfund.domain.configurator.validator.ScannerConfigValidator;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerConfigurator {
    DatasourceRepository datasourceRepository;
    ScannerConfigRepository scannerConfigRepository;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;

    public synchronized void addNewScanner(final SaveScannerCommand command) {
        loggerFacade.logRunCreateSignalScanner(command);
        ScannerConfig scannerConfig = ScannerConfig.from(uuidProvider.generate(), command);
        validate(command.getDatasourceId(), scannerConfig);
        scannerConfigRepository.save(scannerConfig);
        loggerFacade.logSaveNewDataScanner(scannerConfig.getId());
    }

    public synchronized void updateScanner(final UUID scannerId, final SaveScannerCommand command) {
        if (!scannerConfigRepository.existsBy(scannerId)) {
            throw new ApplicationException("Сканер сигналов с идентификатором " + scannerId + " не найден.");
        }
        ScannerConfig scannerConfig = ScannerConfig.from(scannerId, command);
        validate(command.getDatasourceId(), scannerConfig);
        scannerConfigRepository.save(scannerConfig);
        loggerFacade.logUpdateSignalScanner(scannerId);
    }

    private void validate(UUID command, ScannerConfig scannerConfig) {
        if (scannerConfig.getDescription().equals("Сканер сигналов с алгоритмом \"Аномальные объемы\": TGKN, TGKB, индекс IMOEX.")) {
            System.out.println("LOH");
        }
        Datasource datasource = datasourceRepository
            .getBy(command)
            .orElseThrow(() -> new ApplicationException("Источник данных не найден."));
        new ScannerConfigValidator(datasource.getTickers(), scannerConfig).validate();
    }
}
