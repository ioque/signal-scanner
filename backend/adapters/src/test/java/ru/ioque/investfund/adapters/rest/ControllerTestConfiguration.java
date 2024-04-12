package ru.ioque.investfund.adapters.rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import ru.ioque.investfund.adapters.persistence.DatasourceQueryRepository;
import ru.ioque.investfund.adapters.persistence.PsqlScannerRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaArchivedIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaScannerLogRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalScannerRepository;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.modules.datasource.DatasourceManager;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;

import static org.mockito.Mockito.mock;

@Order(1)
@TestConfiguration
public class ControllerTestConfiguration {
    @Bean
    @Primary
    public DatasourceManager mockAggregatorManager() {
        return mock(DatasourceManager.class);
    }

    @Bean
    @Primary
    public ScannerManager mockSignalProducerManager() {
        return mock(ScannerManager.class);
    }

    @Bean
    @Primary
    public PsqlScannerRepository signalProducerRepo() {
        return mock(PsqlScannerRepository.class);
    }

    @Bean
    @Primary
    public JpaSignalScannerRepository mockSignalScannerRepository() {
        return mock(JpaSignalScannerRepository.class);
    }

    @Bean
    @Primary
    public JpaIntradayValueRepository mockDealDataRepository() {
        return mock(JpaIntradayValueRepository.class);
    }

    @Bean
    @Primary
    public JpaHistoryValueRepository mockHistoryTradeDataRepository() {
        return mock(JpaHistoryValueRepository.class);
    }

    @Bean
    @Primary
    public JpaInstrumentRepository mockInstrumentEntityRepository() {
        return mock(JpaInstrumentRepository.class);
    }

    @Bean
    @Primary
    public DatasourceRepository mockExchangeRepository() {
        return mock(DatasourceRepository.class);
    }

    @Bean
    @Primary
    public DatasourceQueryRepository mockDatasourceQueryService() {
        return mock(DatasourceQueryRepository.class);
    }

    @Bean
    @Primary
    public JpaDatasourceRepository mockExchangeEntityRepository() {
        return mock(JpaDatasourceRepository.class);
    }

    @Bean
    @Primary
    public JpaScannerLogRepository mockScannerLogEntityRepository() {
        return mock(JpaScannerLogRepository.class);
    }

    @Bean
    @Primary
    public JpaArchivedIntradayValueRepository mockArchivedIntradayValueEntityRepository() {
        return mock(JpaArchivedIntradayValueRepository.class);
    }

    @Bean
    @Primary
    public JpaSignalRepository mockSignalEntityRepository() {
        return mock(JpaSignalRepository.class);
    }

    @Bean
    @Primary
    public DateTimeProvider mockDateTimeProvider() {
        return mock(DateTimeProvider.class);
    }

    @Bean
    @Primary
    public EventPublisher mockEventBus() {
        return mock(EventPublisher.class);
    }
}
