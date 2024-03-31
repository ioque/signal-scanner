package ru.ioque.investfund.adapters.rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import ru.ioque.investfund.adapters.storage.jpa.JpaInstrumentQueryRepository;
import ru.ioque.investfund.adapters.storage.jpa.JpaScannerRepo;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedHistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedIntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.HistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ScannerLogEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;

import static org.mockito.Mockito.mock;

@Order(1)
@TestConfiguration
public class ControllerTestConfiguration {

    @Bean
    @Primary
    public ExchangeManager mockAggregatorManager() {
        return mock(ExchangeManager.class);
    }

    @Bean
    @Primary
    public ScannerManager mockSignalProducerManager() {
        return mock(ScannerManager.class);
    }

    @Bean
    @Primary
    public JpaScannerRepo signalProducerRepo() {
        return mock(JpaScannerRepo.class);
    }

    @Bean
    @Primary
    public SignalScannerEntityRepository mockSignalScannerRepository() {
        return mock(SignalScannerEntityRepository.class);
    }

    @Bean
    @Primary
    public IntradayValueEntityRepository mockDealDataRepository() {
        return mock(IntradayValueEntityRepository.class);
    }

    @Bean
    @Primary
    public HistoryValueEntityRepository mockHistoryTradeDataRepository() {
        return mock(HistoryValueEntityRepository.class);
    }

    @Bean
    @Primary
    public InstrumentEntityRepository mockInstrumentEntityRepository() {
        return mock(InstrumentEntityRepository.class);
    }

    @Bean
    @Primary
    public ExchangeRepository mockExchangeRepository() {
        return mock(ExchangeRepository.class);
    }

    @Bean
    @Primary
    public JpaInstrumentQueryRepository mockInstrumentRepository() {
        return mock(JpaInstrumentQueryRepository.class);
    }

    @Bean
    @Primary
    public ExchangeEntityRepository mockExchangeEntityRepository() {
        return mock(ExchangeEntityRepository.class);
    }

    @Bean
    @Primary
    public ScannerLogEntityRepository mockScannerLogEntityRepository() {
        return mock(ScannerLogEntityRepository.class);
    }

    @Bean
    @Primary
    public ArchivedIntradayValueEntityRepository mockArchivedIntradayValueEntityRepository() {
        return mock(ArchivedIntradayValueEntityRepository.class);
    }

    @Bean
    @Primary
    public ArchivedHistoryValueEntityRepository mockArchivedDailyValueEntityRepository() {
        return mock(ArchivedHistoryValueEntityRepository.class);
    }

    @Bean
    @Primary
    public SignalEntityRepository mockSignalEntityRepository() {
        return mock(SignalEntityRepository.class);
    }

    @Bean
    @Primary
    public DateTimeProvider mockDateTimeProvider() {
        return mock(DateTimeProvider.class);
    }

    @Bean
    @Primary
    public EventBus mockEventBus() {
        return mock(EventBus.class);
    }
}
