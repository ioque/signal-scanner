package ru.ioque.investfund.adapters.rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import ru.ioque.investfund.adapters.storage.jpa.JpaScannerRepo;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ReportEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ScheduleUnitEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.InstrumentQueryRepository;
import ru.ioque.investfund.application.adapters.ReportRepository;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.application.modules.schedule.ScheduleManager;
import ru.ioque.investfund.application.adapters.ScheduleRepository;

import static org.mockito.Mockito.mock;

@Order(1)
@TestConfiguration
public class ControllerTestConfiguration {
    @Bean
    @Primary
    public ScheduleManager mockScheduleManager() {
        return mock(ScheduleManager.class);
    }

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
    public ScheduleUnitEntityRepository mockScheduleUnitRepository() {
        return mock(ScheduleUnitEntityRepository.class);
    }

    @Bean
    @Primary
    public IntradayValueEntityRepository mockDealDataRepository() {
        return mock(IntradayValueEntityRepository.class);
    }

    @Bean
    @Primary
    public DailyValueEntityRepository mockHistoryTradeDataRepository() {
        return mock(DailyValueEntityRepository.class);
    }

    @Bean
    @Primary
    public InstrumentEntityRepository mockInstrumentEntityRepository() {
        return mock(InstrumentEntityRepository.class);
    }

    @Bean
    @Primary
    public ScheduleRepository mockScheduleRepo() {
        return mock(ScheduleRepository.class);
    }

    @Bean
    @Primary
    public ExchangeRepository mockExchangeRepository() {
        return mock(ExchangeRepository.class);
    }

    @Bean
    @Primary
    public InstrumentQueryRepository mockInstrumentRepository() {
        return mock(InstrumentQueryRepository.class);
    }

    @Bean
    @Primary
    public ExchangeEntityRepository mockExchangeEntityRepository() {
        return mock(ExchangeEntityRepository.class);
    }

    @Bean
    @Primary
    public ReportRepository mockReportRepository() {
        return mock(ReportRepository.class);
    }

    @Bean
    @Primary
    public ReportEntityRepository mockReportEntityRepository() {
        return mock(ReportEntityRepository.class);
    }
}
