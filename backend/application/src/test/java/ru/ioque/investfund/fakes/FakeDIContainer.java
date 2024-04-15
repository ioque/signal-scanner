package ru.ioque.investfund.fakes;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.modules.CommandBus;
import ru.ioque.investfund.application.modules.datasource.DisableUpdateInstrumentProcessor;
import ru.ioque.investfund.application.modules.datasource.EnableUpdateInstrumentProcessor;
import ru.ioque.investfund.application.modules.datasource.IntegrateInstrumentsProcessor;
import ru.ioque.investfund.application.modules.datasource.IntegrateTradingDataProcessor;
import ru.ioque.investfund.application.modules.datasource.RegisterDatasourceProcessor;
import ru.ioque.investfund.application.modules.datasource.UnregisterDatasourceProcessor;
import ru.ioque.investfund.application.modules.datasource.UpdateDatasourceProcessor;
import ru.ioque.investfund.application.modules.scanner.CreateScannerProcessor;
import ru.ioque.investfund.application.modules.scanner.ProduceSignalProcessor;
import ru.ioque.investfund.application.modules.scanner.UpdateScannerProcessor;
import ru.ioque.investfund.fixture.ExchangeDataFixture;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FakeDIContainer {
    ExchangeDataFixture exchangeDataFixture;
    FakeDateTimeProvider dateTimeProvider;
    FakeDatasourceProvider exchangeProvider;
    FakeLoggerProvider loggerProvider;
    FakeUUIDProvider uuidProvider;
    FakeTradingDataRepository tradingDataRepository;
    FakeScannerLogRepository scannerLogRepository;
    FakeScannerRepository scannerRepository;
    FakeDatasourceRepository datasourceRepository;
    FakeEventPublisher eventPublisher;
    DisableUpdateInstrumentProcessor disableUpdateInstrumentProcessor;
    EnableUpdateInstrumentProcessor enableUpdateInstrumentProcessor;
    IntegrateInstrumentsProcessor integrateInstrumentsProcessor;
    IntegrateTradingDataProcessor integrateTradingDataProcessor;
    RegisterDatasourceProcessor registerDatasourceProcessor;
    UnregisterDatasourceProcessor unregisterDatasourceProcessor;
    UpdateDatasourceProcessor updateDatasourceProcessor;
    CreateScannerProcessor createScannerProcessor;
    ProduceSignalProcessor produceSignalProcessor;
    UpdateScannerProcessor  updateScannerProcessor;
    CommandBus commandBus;

    public FakeDIContainer() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        eventPublisher = new FakeEventPublisher();
        dateTimeProvider = new FakeDateTimeProvider();
        exchangeDataFixture = new ExchangeDataFixture();
        exchangeProvider = getFakeExchangeProvider();
        loggerProvider = new FakeLoggerProvider();
        uuidProvider = new FakeUUIDProvider();
        scannerLogRepository = new FakeScannerLogRepository();
        datasourceRepository = new FakeDatasourceRepository();
        scannerRepository = new FakeScannerRepository();
        tradingDataRepository = new FakeTradingDataRepository(datasourceRepository, dateTimeProvider);

        disableUpdateInstrumentProcessor = new DisableUpdateInstrumentProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        enableUpdateInstrumentProcessor = new EnableUpdateInstrumentProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        integrateInstrumentsProcessor = new IntegrateInstrumentsProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            exchangeProvider,
            datasourceRepository
        );
        integrateTradingDataProcessor = new IntegrateTradingDataProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            exchangeProvider,
            datasourceRepository,
            eventPublisher
        );
        registerDatasourceProcessor = new RegisterDatasourceProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            datasourceRepository
        );
        unregisterDatasourceProcessor = new UnregisterDatasourceProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        updateDatasourceProcessor = new UpdateDatasourceProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            datasourceRepository
        );
        createScannerProcessor = new CreateScannerProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            uuidProvider,
            scannerRepository,
            datasourceRepository
        );
        updateScannerProcessor = new UpdateScannerProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            datasourceRepository
        );
        produceSignalProcessor = new ProduceSignalProcessor(
            dateTimeProvider,
            validator,
            loggerProvider,
            scannerRepository,
            tradingDataRepository,
            eventPublisher
        );

        commandBus = new CommandBus(
            List.of(
                disableUpdateInstrumentProcessor,
                enableUpdateInstrumentProcessor,
                integrateInstrumentsProcessor,
                integrateTradingDataProcessor,
                registerDatasourceProcessor,
                updateDatasourceProcessor,
                unregisterDatasourceProcessor,
                createScannerProcessor,
                produceSignalProcessor,
                updateScannerProcessor
            )
        );
    }

    private FakeDatasourceProvider getFakeExchangeProvider() {
        FakeDatasourceProvider fakeExchangeProvider = new FakeDatasourceProvider(dateTimeProvider);
        fakeExchangeProvider.setExchangeDataFixture(exchangeDataFixture);
        return fakeExchangeProvider;
    }
}