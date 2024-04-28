package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.integration.InfrastructureTest;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

@Transactional
public abstract class DatabaseTest extends InfrastructureTest {

    @Autowired
    protected DateTimeProvider dateTimeProvider;
    @Autowired
    protected DatasourceRepository datasourceRepository;
    @Autowired
    protected IntradayValueRepository intradayValueRepository;
    @Autowired
    protected JpaDatasourceRepository jpaDatasourceRepository;
    @Autowired
    protected JpaInstrumentRepository jpaInstrumentRepository;
    @Autowired
    protected JpaIntradayValueRepository jpaIntradayValueRepository;

    @BeforeEach
    void beforeEach() {
        jpaDatasourceRepository.deleteAll();
        jpaInstrumentRepository.deleteAll();
        jpaIntradayValueRepository.deleteAll();
    }

    protected void prepareState() {
        dateTimeProvider.initToday(LocalDateTime.parse("2024-04-04T12:00:00"));
        Datasource moexDatasource = moexDatasource();
        Datasource nasdaqDatasource = nasdaqDatasource();
        moexDatasource.getInstruments().forEach(instrument -> {
            instrument.updateTradingState(
                new TreeSet<>(
                    List.of(
                        createDeal(instrument.getTicker(),1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                        createDeal(instrument.getTicker(),2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                        createDeal(instrument.getTicker(),3L, LocalDateTime.parse("2024-04-04T12:00:00"))
                    )
                )
            );
            instrument.updateAggregateHistory(
                new TreeSet<>(
                    List.of(
                        createHistoryValue(LocalDate.parse("2024-04-01")),
                        createHistoryValue(LocalDate.parse("2024-04-02")),
                        createHistoryValue(LocalDate.parse("2024-04-03"))
                    )
                )
            );
        });
        nasdaqDatasource.getInstruments().forEach(instrument -> {
            instrument.updateTradingState(
                new TreeSet<>(
                    List.of(
                        createDeal(instrument.getTicker(),1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                        createDeal(instrument.getTicker(),2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                        createDeal(instrument.getTicker(),3L, LocalDateTime.parse("2024-04-04T12:00:00"))
                    )
                )
            );
            instrument.updateAggregateHistory(
                new TreeSet<>(
                    List.of(
                        createHistoryValue(LocalDate.parse("2024-04-01")),
                        createHistoryValue(LocalDate.parse("2024-04-02")),
                        createHistoryValue(LocalDate.parse("2024-04-03"))
                    )
                )
            );
        });
        datasourceRepository.save(moexDatasource);
        datasourceRepository.save(nasdaqDatasource());
    }
}