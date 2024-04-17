package ru.ioque.investfund.adapters.unit.rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import ru.ioque.investfund.adapters.persistence.repositories.JpaArchivedIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaSignalScannerRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaTelegramChatRepository;
import ru.ioque.investfund.adapters.query.PsqlDatasourceQueryService;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventPublisher;
import ru.ioque.investfund.application.adapters.ScannerRepository;
import ru.ioque.investfund.application.adapters.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.modules.CommandBus;

import static org.mockito.Mockito.mock;

@Order(1)
@TestConfiguration
public class ControllerTestConfiguration {
    @Bean
    @Primary
    public CommandBus mockCommandBus() {
        return mock(CommandBus.class);
    }

    @Bean
    @Primary
    public EventPublisher mockEventPublisher() {
        return mock(EventPublisher.class);
    }

    @Bean
    @Primary
    public TelegramMessageSender mockTelegramMessageSender() {
        return mock(TelegramMessageSender.class);
    }

    @Bean
    @Primary
    public ScannerRepository signalScannerRepository() {
        return mock(ScannerRepository.class);
    }

    @Bean
    @Primary
    public TelegramChatRepository signalTelegramChatRepository() {
        return mock(TelegramChatRepository.class);
    }

    @Bean
    @Primary
    public JpaTelegramChatRepository signalJpaTelegramChatRepository() {
        return mock(JpaTelegramChatRepository.class);
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
    public PsqlDatasourceQueryService mockDatasourceQueryService() {
        return mock(PsqlDatasourceQueryService.class);
    }

    @Bean
    @Primary
    public JpaDatasourceRepository mockExchangeEntityRepository() {
        return mock(JpaDatasourceRepository.class);
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
}
