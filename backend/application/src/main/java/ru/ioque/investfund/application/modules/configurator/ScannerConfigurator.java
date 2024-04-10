package ru.ioque.investfund.application.modules.configurator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.ScannerConfigRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.application.share.logger.LoggerFacade;
import ru.ioque.investfund.domain.configurator.SignalScannerConfig;

import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScannerConfigurator {
    ScannerConfiguratorCommandValidator scannerConfiguratorCommandValidator;
    ScannerConfigRepository scannerConfigRepository;
    UUIDProvider uuidProvider;
    LoggerFacade loggerFacade;

    public synchronized void addNewScanner(final AddNewScannerCommand command) {
        scannerConfiguratorCommandValidator.validate(command);
        loggerFacade.logRunCreateSignalScanner(command);
        final UUID id = uuidProvider.generate();
        scannerConfigRepository.save(
            SignalScannerConfig.builder()
                .id(id)
                .datasourceId(command.getDatasourceId())
                .workPeriodInMinutes(command.getWorkPeriodInMinutes())
                .description(command.getDescription())
                .algorithmConfig(command.getAlgorithmConfig())
                .tickers(command.getTickers())
                .build()
        );
        loggerFacade.logSaveNewDataScanner(id);
    }

    public synchronized void updateScanner(final UpdateScannerCommand command) {
        if (!scannerConfigRepository.existsBy(command.getId())) {
            throw new ApplicationException("Сканер сигналов с идентификатором " + command.getId() + " не найден.");
        }
        scannerConfigRepository.save(
            SignalScannerConfig.builder()
                .id(command.getId())
                .datasourceId(command.getDatasourceId())
                .workPeriodInMinutes(command.getWorkPeriodInMinutes())
                .description(command.getDescription())
                .algorithmConfig(command.getAlgorithmConfig())
                .tickers(command.getTickers())
                .build()
        );
        loggerFacade.logUpdateSignalScanner(command);
    }
}
