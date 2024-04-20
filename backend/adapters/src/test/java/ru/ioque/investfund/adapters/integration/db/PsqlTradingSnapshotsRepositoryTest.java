package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.PsqlTradingSnapshotsRepository;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("PSQL TRADING SNAPSHOTS REPOSITORY TEST")
public class PsqlTradingSnapshotsRepositoryTest extends DatabaseTest {
    private final PsqlTradingSnapshotsRepository tradingSnapshotsRepository;

    public PsqlTradingSnapshotsRepositoryTest(@Autowired PsqlTradingSnapshotsRepository tradingSnapshotsRepository) {
        this.tradingSnapshotsRepository = tradingSnapshotsRepository;
    }

    @Test
    @DisplayName("""
        T1. Получение снэпшота торговых данных.
        """)
    void testCase1() {
        initDatabase();

        final List<TradingSnapshot> tradingSnapshots = tradingSnapshotsRepository.findAllBy(List.of(TGKN_ID));

        assertEquals(1, tradingSnapshots.size());
        TradingSnapshot tgkn = tradingSnapshots.get(0);
        assertEquals(TGKN_ID, tgkn.getInstrumentId());
        assertEquals(3, tgkn.getTodayPriceSeries().size());
        assertEquals(3, tgkn.getClosePriceSeries().size());
        assertEquals(3, tgkn.getOpenPriceSeries().size());
        assertEquals(3, tgkn.getWaPriceSeries().size());
        assertEquals(3, tgkn.getValueSeries().size());
        assertEquals(3, tgkn.getTodayValueSeries().size());
        tgkn.getTodayPriceSeries().forEach(series -> assertEquals(1.0, series.getValue()));
        tgkn.getClosePriceSeries().forEach(series -> assertEquals(1.0, series.getValue()));
        tgkn.getOpenPriceSeries().forEach(series -> assertEquals(1.0, series.getValue()));
        tgkn.getWaPriceSeries().forEach(series -> assertEquals(1.0, series.getValue()));
        tgkn.getValueSeries().forEach(series -> assertEquals(1.0, series.getValue()));
        tgkn.getTodayValueSeries().forEach(series -> assertEquals(1.0, series.getValue()));
    }

    private void initDatabase() {
        dateTimeProvider.initToday(LocalDateTime.parse("2024-04-04T12:00:00"));
        datasourceRepository.save(moexDatasource());
        historyValueRepository.saveAll(
            List.of(
                createHistoryValue(TGKN, LocalDate.parse("2024-04-01")),
                createHistoryValue(TGKN, LocalDate.parse("2024-04-02")),
                createHistoryValue(TGKN, LocalDate.parse("2024-04-03"))
            )
        );
        intradayValueRepository.saveAll(
            List.of(
                createDeal(TGKN,1L, LocalDateTime.parse("2024-04-04T10:00:00")),
                createDeal(TGKN,2L, LocalDateTime.parse("2024-04-04T11:00:00")),
                createDeal(TGKN,3L, LocalDateTime.parse("2024-04-04T12:00:00"))
            )
        );
    }
}
