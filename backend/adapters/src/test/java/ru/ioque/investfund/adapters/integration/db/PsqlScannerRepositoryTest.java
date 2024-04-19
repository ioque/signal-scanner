package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.PsqlScannerRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PSQL SCANNER REPOSITORY TEST")
public class PsqlScannerRepositoryTest extends DatabaseTest {
    PsqlScannerRepository psqlScannerRepository;

    public PsqlScannerRepositoryTest(
        @Autowired PsqlScannerRepository psqlScannerRepository
    ) {
        this.psqlScannerRepository = psqlScannerRepository;
    }

    @BeforeEach
    void beforeEach() {
        prepareState();
    }

    @Test
    @DisplayName("""
        T1. Сохранение сканера аномальных объемов
        """)
    void testCase1() {
        final ScannerId scannerId = ScannerId.from(UUID.randomUUID());
        final List<InstrumentId> instrumentIds = List.of(TGKN_ID, TGKB_ID, IMOEX_ID);
        final AnomalyVolumeProperties properties = createAnomalyVolumeProperties(1.5, 180, IMOEX_ID);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final LocalDateTime lastExecutionDateTime = LocalDateTime.parse("2024-01-01T10:00:00");
        final Signal signal = Signal.builder()
            .price(10D)
            .instrumentId(instrumentIds.get(0))
            .isOpen(true)
            .isBuy(true)
            .dateTime(lastExecutionDateTime)
            .build();
        final SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .datasourceId(MOEX_DATASOURCE_ID)
            .instrumentIds(instrumentIds)
            .properties(properties)
            .description(desc)
            .signals(new ArrayList<>(List.of(signal)))
            .workPeriodInMinutes(workPeriodInMinutes)
            .lastExecutionDateTime(lastExecutionDateTime)
            .build();

        psqlScannerRepository.save(scanner);

        assertEquals(scanner, psqlScannerRepository.findBy(scannerId).orElseThrow());
    }

    @Test
    @DisplayName("""
        T2. Сохранение сканера преф-обычка
        """)
    void testCase2() {
        final ScannerId scannerId = ScannerId.from(UUID.randomUUID());
        final List<InstrumentId> instrumentIds = List.of(TGKN_ID, TGKB_ID, IMOEX_ID);
        final PrefCommonProperties properties = createPrefCommonProperties(1.5);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final LocalDateTime lastExecutionDateTime = LocalDateTime.parse("2024-01-01T10:00:00");
        final Signal signal = Signal.builder()
            .price(10D)
            .instrumentId(instrumentIds.get(0))
            .isOpen(true)
            .isBuy(true)
            .dateTime(lastExecutionDateTime)
            .build();
        final SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .datasourceId(MOEX_DATASOURCE_ID)
            .instrumentIds(instrumentIds)
            .properties(properties)
            .description(desc)
            .signals(new ArrayList<>(List.of(signal)))
            .workPeriodInMinutes(workPeriodInMinutes)
            .lastExecutionDateTime(lastExecutionDateTime)
            .build();

        psqlScannerRepository.save(scanner);

        assertEquals(scanner, psqlScannerRepository.findBy(scannerId).orElseThrow());
    }

    @Test
    @DisplayName("""
        T3. Сохранение сканера фьючерс сектора
        """)
    void testCase3() {
        final ScannerId scannerId = ScannerId.from(UUID.randomUUID());
        final List<InstrumentId> tickers = List.of(TGKB_ID, BRF4_ID);
        final SectoralFuturesProperties properties = createSectoralFuturesProperties(0.015, 0.015, BRF4_ID);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final LocalDateTime lastExecutionDateTime = LocalDateTime.parse("2024-01-01T10:00:00");
        final Signal signal = Signal.builder()
            .price(10D)
            .instrumentId(tickers.get(0))
            .isOpen(true)
            .isBuy(true)
            .dateTime(lastExecutionDateTime)
            .build();
        final SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .datasourceId(MOEX_DATASOURCE_ID)
            .instrumentIds(tickers)
            .properties(properties)
            .description(desc)
            .signals(new ArrayList<>(List.of(signal)))
            .workPeriodInMinutes(workPeriodInMinutes)
            .lastExecutionDateTime(lastExecutionDateTime)
            .build();

        psqlScannerRepository.save(scanner);

        assertEquals(scanner, psqlScannerRepository.findBy(scannerId).orElseThrow());
    }

    @Test
    @DisplayName("""
        T4. Сохранение сканера секторальный отстающий
        """)
    void testCase4() {
        final ScannerId scannerId = ScannerId.from(UUID.randomUUID());
        final List<InstrumentId> tickers = List.of(TGKN_ID, TGKB_ID, IMOEX_ID);
        final SectoralRetardProperties properties = createSectoralRetardProperties(0.015, 0.015);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final LocalDateTime lastExecutionDateTime = LocalDateTime.parse("2024-01-01T10:00:00");
        final Signal signal = Signal.builder()
            .price(10D)
            .instrumentId(tickers.get(0))
            .isOpen(true)
            .isBuy(true)
            .dateTime(lastExecutionDateTime)
            .build();
        final SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .datasourceId(MOEX_DATASOURCE_ID)
            .instrumentIds(tickers)
            .properties(properties)
            .description(desc)
            .signals(new ArrayList<>(List.of(signal)))
            .workPeriodInMinutes(workPeriodInMinutes)
            .lastExecutionDateTime(lastExecutionDateTime)
            .build();

        psqlScannerRepository.save(scanner);

        assertEquals(scanner, psqlScannerRepository.findBy(scannerId).orElseThrow());
    }

    private SectoralRetardProperties createSectoralRetardProperties(double historyScale, double intradayScale) {
        return new SectoralRetardProperties(historyScale, intradayScale);
    }

    @Test
    @DisplayName("""
        T5. Выгрузка сканеров по идентификатору источника данных
        """)
    void testCase5() {
        final SignalScanner moexAnomalyVolumeScanner = creatScanner(
            ScannerId.from(UUID.randomUUID()),
            MOEX_DATASOURCE_ID,
            List.of(TGKN_ID, TGKB_ID, IMOEX_ID),
            createAnomalyVolumeProperties(1.5, 180, IMOEX_ID)
        );
        final SignalScanner prefCommonNasdaqScanner = creatScanner(
            ScannerId.from(UUID.randomUUID()),
            NASDAQ_DATASOURCE_ID,
            List.of(APPL_ID, APPLP_ID),
            createPrefCommonProperties(1.0)
        );
        psqlScannerRepository.save(moexAnomalyVolumeScanner);
        psqlScannerRepository.save(prefCommonNasdaqScanner);

        assertEquals(1, psqlScannerRepository.findAllBy(NASDAQ_DATASOURCE_ID).size());
        assertTrue(psqlScannerRepository.findAllBy(NASDAQ_DATASOURCE_ID).contains(prefCommonNasdaqScanner));

        assertEquals(1, psqlScannerRepository.findAllBy(MOEX_DATASOURCE_ID).size());
        assertTrue(psqlScannerRepository.findAllBy(MOEX_DATASOURCE_ID).contains(moexAnomalyVolumeScanner));
    }

    private SignalScanner creatScanner(
        ScannerId scannerId,
        DatasourceId datasourceId,
        List<InstrumentId> instrumentIds,
        AlgorithmProperties properties
    ) {
        return SignalScanner.builder()
            .id(scannerId)
            .datasourceId(datasourceId)
            .instrumentIds(instrumentIds)
            .properties(properties)
            .description("desc")
            .signals(new ArrayList<>())
            .workPeriodInMinutes(1)
            .lastExecutionDateTime(LocalDateTime.parse("2024-01-01T10:00:00"))
            .build();
    }

    private PrefCommonProperties createPrefCommonProperties(double spreadValue) {
        return new PrefCommonProperties(spreadValue);
    }

    private AnomalyVolumeProperties createAnomalyVolumeProperties(
        double scaleCoefficient,
        int historyPeriod,
        InstrumentId indexId
    ) {
        return new AnomalyVolumeProperties(scaleCoefficient, historyPeriod, indexId);
    }

    private SectoralFuturesProperties createSectoralFuturesProperties(
        double futuresOvernightScale,
        double stockOvernightScale,
        InstrumentId futuresId
    ) {
        return new SectoralFuturesProperties(futuresOvernightScale, stockOvernightScale, futuresId);
    }
}
