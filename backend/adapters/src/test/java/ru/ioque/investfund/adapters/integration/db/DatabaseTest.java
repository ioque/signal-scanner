package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.integration.InfrastructureTest;
import ru.ioque.investfund.adapters.psql.dao.JpaAggregatedTotalsRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaIntradayValueRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaTelegramChatRepository;
import ru.ioque.investfund.application.adapters.repository.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;

import java.time.LocalDateTime;

@Transactional
public abstract class DatabaseTest extends InfrastructureTest {

    @Autowired
    protected DateTimeProvider dateTimeProvider;
    @Autowired
    protected DatasourceRepository datasourceRepository;
    @Autowired
    protected JpaDatasourceRepository jpaDatasourceRepository;
    @Autowired
    protected JpaInstrumentRepository jpaInstrumentRepository;
    @Autowired
    protected JpaIntradayValueRepository jpaIntradayValueRepository;
    @Autowired
    protected JpaAggregatedTotalsRepository jpaAggregatedTotalsRepository;
    @Autowired
    protected JpaTelegramChatRepository jpaTelegramChatRepository;

    @BeforeEach
    void beforeEach() {
        jpaDatasourceRepository.deleteAll();
        jpaInstrumentRepository.deleteAll();
        jpaIntradayValueRepository.deleteAll();
        jpaAggregatedTotalsRepository.deleteAll();
        jpaTelegramChatRepository.deleteAll();
    }

    protected void initializeDatasource() {
        dateTimeProvider.initToday(LocalDateTime.parse("2024-04-04T12:00:00"));
        datasourceRepository.save(moexDatasource());
        datasourceRepository.save(nasdaqDatasource());
    }
}