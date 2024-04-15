package ru.ioque.investfund.application.modules.scanner;

import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.LoggerProvider;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.CommandProcessor;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

import java.util.UUID;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateScannerProcessor extends CommandProcessor<CreateScannerCommand> {
    UUIDProvider uuidProvider;
    ScannerRepository scannerRepository;
    DatasourceRepository datasourceRepository;

    public CreateScannerProcessor(
        DateTimeProvider dateTimeProvider,
        Validator validator,
        LoggerProvider loggerProvider,
        UUIDProvider uuidProvider,
        ScannerRepository scannerRepository,
        DatasourceRepository datasourceRepository
    ) {
        super(dateTimeProvider, validator, loggerProvider);
        this.uuidProvider = uuidProvider;
        this.dateTimeProvider = dateTimeProvider;
        this.datasourceRepository = datasourceRepository;
        this.scannerRepository = scannerRepository;
    }

    @Override
    protected void handleFor(CreateScannerCommand command) {
        final Datasource datasource = getDatasource(command.getDatasourceId());
        datasource.checkExistsTickers(command.getTickers());
        final UUID newScannerId = uuidProvider.generate();
        SignalScanner scanner = SignalScanner.of(newScannerId, command);
        scannerRepository.save(scanner);
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
