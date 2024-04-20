package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.ioque.investfund.adapters.integration.InfrastructureTest;
import ru.ioque.investfund.adapters.persistence.repositories.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaHistoryValueRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaInstrumentRepository;
import ru.ioque.investfund.adapters.persistence.repositories.JpaIntradayValueRepository;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public abstract class DatabaseTest extends InfrastructureTest {

    @Autowired
    protected DateTimeProvider dateTimeProvider;
    @Autowired
    protected UUIDProvider uuidProvider;
    @Autowired
    protected DatasourceRepository datasourceRepository;
    @Autowired
    protected HistoryValueRepository historyValueRepository;
    @Autowired
    protected IntradayValueRepository intradayValueRepository;
    @Autowired
    protected JpaDatasourceRepository jpaDatasourceRepository;
    @Autowired
    protected JpaInstrumentRepository jpaInstrumentRepository;
    @Autowired
    protected JpaHistoryValueRepository jpaHistoryValueRepository;
    @Autowired
    protected JpaIntradayValueRepository jpaIntradayValueRepository;

    @BeforeEach
    void beforeEach() {
        jpaDatasourceRepository.deleteAll();
        jpaInstrumentRepository.deleteAll();
        jpaIntradayValueRepository.deleteAll();
        jpaHistoryValueRepository.deleteAll();
    }

    protected void prepareState() {
        dateTimeProvider.initToday(LocalDateTime.parse("2024-04-04T12:00:00"));
        datasourceRepository.save(moexDatasource());
        datasourceRepository.save(nasdaqDatasource());
        historyValueRepository.saveAll(
            List.of(
                createHistoryValue(TGKN, LocalDate.parse("2024-04-01")),
                createHistoryValue(TGKN, LocalDate.parse("2024-04-02")),
                createHistoryValue(TGKN, LocalDate.parse("2024-04-03")),
                createHistoryValue(TGKB, LocalDate.parse("2024-04-01")),
                createHistoryValue(TGKB, LocalDate.parse("2024-04-02")),
                createHistoryValue(TGKB, LocalDate.parse("2024-04-03")),
                createHistoryValue(IMOEX, LocalDate.parse("2024-04-01")),
                createHistoryValue(IMOEX, LocalDate.parse("2024-04-02")),
                createHistoryValue(IMOEX, LocalDate.parse("2024-04-03")),
                createHistoryValue(APPL, LocalDate.parse("2024-04-01")),
                createHistoryValue(APPL, LocalDate.parse("2024-04-02")),
                createHistoryValue(APPL, LocalDate.parse("2024-04-03")),
                createHistoryValue(APPLP, LocalDate.parse("2024-04-01")),
                createHistoryValue(APPLP, LocalDate.parse("2024-04-02")),
                createHistoryValue(APPLP, LocalDate.parse("2024-04-03")),
                createHistoryValue(COMP, LocalDate.parse("2024-04-01")),
                createHistoryValue(COMP, LocalDate.parse("2024-04-02")),
                createHistoryValue(COMP, LocalDate.parse("2024-04-03"))
            )
        );
        intradayValueRepository.saveAll(
            List.of(
                createDeal(TGKN,1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(TGKN,2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(TGKN,3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(TGKB,1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(TGKB,2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(TGKB,3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDelta(IMOEX, 1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDelta(IMOEX, 2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDelta(IMOEX, 3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(APPL, 1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(APPL, 2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(APPL, 3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDeal(APPLP, 1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(APPLP, 2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(APPLP, 3L, LocalDateTime.parse("2024-04-04T12:00:00")),
                createDelta(COMP, 1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDelta(COMP, 2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDelta(COMP, 3L, LocalDateTime.parse("2024-04-04T12:00:00"))
            )
        );
    }
}