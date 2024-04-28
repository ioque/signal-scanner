package ru.ioque.investfund.application.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.api.command.CommandHandler;
import ru.ioque.investfund.application.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.core.ApplicationLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerCommandHandler extends CommandHandler<UpdateScannerCommand> {
    ScannerRepository scannerRepository;
    DatasourceRepository datasourceRepository;

    public UpdateScannerCommandHandler(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        ScannerRepository scannerRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.scannerRepository = scannerRepository;
        this.datasourceRepository = datasourceRepository;
    }

    @Override
    protected List<ApplicationLog> businessProcess(UpdateScannerCommand command) {
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        final Datasource datasource = datasourceRepository.getBy(scanner.getDatasourceId());
        final List<InstrumentId> instrumentIds = datasource.findInstrumentIds(command.getTickers());
        scanner.updateWorkPeriod(command.getWorkPeriodInMinutes());
        scanner.updateDescription(command.getDescription());
        scanner.updateProperties(command.getProperties());
        scanner.updateInstrumentIds(instrumentIds);
        scannerRepository.save(scanner);
        return List.of(new InfoLog(
            dateTimeProvider.nowDateTime(),
            String.format("Обновлен сканер сигналов[id=%s]", scanner.getId())
        ));
    }
}
