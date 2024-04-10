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
import ru.ioque.investfund.domain.configurator.command.AddNewScannerCommand;
import ru.ioque.investfund.domain.configurator.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.configurator.entity.ScannerConfig;
import ru.ioque.investfund.domain.configurator.validator.ScannerConfigValidator;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerConfigurator {
    DatasourceRepository datasourceRepository;
    ScannerConfigRepository scannerConfigRepository;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;

    public synchronized void addNewScanner(final AddNewScannerCommand command) {
        loggerFacade.logRunCreateSignalScanner(command);
        Datasource datasource = getDatasource(command.getDatasourceId());
        ScannerConfig scannerConfig = ScannerConfig.builder()
            .id(uuidProvider.generate())
            .datasourceId(command.getDatasourceId())
            .workPeriodInMinutes(command.getWorkPeriodInMinutes())
            .description(command.getDescription())
            .algorithmConfig(command.getAlgorithmConfig())
            .tickers(command.getTickers())
            .build();
        ScannerConfigValidator validator = new ScannerConfigValidator(
            datasource.getInstruments().stream().map(Instrument::getTicker).toList(),
            scannerConfig
        );
        validator.validate();
        scannerConfigRepository.save(scannerConfig);
        loggerFacade.logSaveNewDataScanner(scannerConfig.getId());
    }

    public synchronized void updateScanner(final UpdateScannerCommand command) {
        if (!scannerConfigRepository.existsBy(command.getId())) {
            throw new ApplicationException("Сканер сигналов с идентификатором " + command.getId() + " не найден.");
        }
        Datasource datasource = getDatasource(command.getDatasourceId());
        ScannerConfig scannerConfig = ScannerConfig.builder()
            .id(command.getId())
            .datasourceId(command.getDatasourceId())
            .workPeriodInMinutes(command.getWorkPeriodInMinutes())
            .description(command.getDescription())
            .algorithmConfig(command.getAlgorithmConfig())
            .tickers(command.getTickers())
            .build();
        ScannerConfigValidator validator = new ScannerConfigValidator(
            datasource.getInstruments().stream().map(Instrument::getTicker).toList(),
            scannerConfig
        );
        validator.validate();
        scannerConfigRepository.save(scannerConfig);
        loggerFacade.logUpdateSignalScanner(command);
    }

    private Datasource getDatasource(UUID command) {
        return datasourceRepository
            .getBy(command)
            .orElseThrow(() -> new ApplicationException("Источник данных не найден."));
    }
}
