package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.PsqlTradingSnapshotsRepository;
import ru.ioque.investfund.domain.scanner.value.TradingSnapshot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("PSQL TRADING SNAPSHOTS REPOSITORY TEST")
public class PsqlTradingSnapshotsRepositoryTest extends DatabaseTest {
    private final PsqlTradingSnapshotsRepository tradingSnapshotsRepository;

    public PsqlTradingSnapshotsRepositoryTest(@Autowired PsqlTradingSnapshotsRepository tradingSnapshotsRepository) {
        this.tradingSnapshotsRepository = tradingSnapshotsRepository;
    }

    @BeforeEach
    void beforeEach() {
        prepareState();
    }

    @Test
    @DisplayName("""
        T1. Получение снэпшота торговых данных.
        """)
    void testCase1() {
        final List<TradingSnapshot> tradingSnapshots = tradingSnapshotsRepository.findAllBy(List.of(TGKN_ID));

        assertEquals(1, tradingSnapshots.size());
        TradingSnapshot tgkn = tradingSnapshots.get(0);
        assertEquals(TGKN, tgkn.getTicker());
        assertEquals(1.0, tgkn.getLastPrice());
        assertEquals(1.0, tgkn.getFirstPrice());
        assertEquals(3.0, tgkn.getValue());
        assertEquals(3, tgkn.getClosePriceSeries().size());
        assertEquals(3, tgkn.getOpenPriceSeries().size());
        assertEquals(3, tgkn.getWaPriceSeries().size());
        assertEquals(3, tgkn.getValueSeries().size());
        tgkn.getClosePriceSeries().forEach(series -> assertEquals(1.0, series.getValue()));
        tgkn.getOpenPriceSeries().forEach(series -> assertEquals(1.0, series.getValue()));
        tgkn.getWaPriceSeries().forEach(series -> assertEquals(1.0, series.getValue()));
        tgkn.getValueSeries().forEach(series -> assertEquals(1.0, series.getValue()));
    }
}
