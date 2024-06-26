package ru.ioque.investfund.application.modules.scanner.handler;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.modules.api.CommandHandler;
import ru.ioque.investfund.application.modules.api.Result;
import ru.ioque.investfund.application.modules.scanner.command.UpdateScanner;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.List;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateScannerCommandHandler extends CommandHandler<UpdateScanner> {
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
    protected Result businessProcess(UpdateScanner command) {
        final SignalScanner scanner = scannerRepository.getBy(command.getScannerId());
        final Datasource datasource = datasourceRepository.getBy(scanner.getDatasourceId());
        final List<InstrumentId> instrumentIds = datasource.findInstrumentIds(command.getTickers());
        scanner.updateWorkPeriod(command.getWorkPeriodInMinutes());
        scanner.updateDescription(command.getDescription());
        scanner.updateProperties(command.getProperties());
        scanner.updateInstrumentIds(instrumentIds);
        scannerRepository.save(scanner);
        return Result.success();
    }
}
