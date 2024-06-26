package ru.ioque.investfund.adapters.unit.rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import ru.ioque.investfund.adapters.psql.dao.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaSignalRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaScannerRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaTelegramChatRepository;
import ru.ioque.investfund.adapters.service.view.PsqlDatasourceViewService;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.repository.ScannerRepository;
import ru.ioque.investfund.application.adapters.repository.TelegramChatRepository;
import ru.ioque.investfund.application.adapters.TelegramMessageSender;
import ru.ioque.investfund.application.modules.api.CommandBus;

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
    public JpaScannerRepository mockSignalScannerRepository() {
        return mock(JpaScannerRepository.class);
    }

    @Bean
    @Primary
    public JpaIntradayValueRepository mockDealDataRepository() {
        return mock(JpaIntradayValueRepository.class);
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
    public PsqlDatasourceViewService mockDatasourceQueryService() {
        return mock(PsqlDatasourceViewService.class);
    }

    @Bean
    @Primary
    public JpaDatasourceRepository mockExchangeEntityRepository() {
        return mock(JpaDatasourceRepository.class);
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
