package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.PsqlScannerRepository;
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
        final UUID scannerId = UUID.randomUUID();
        final List<String> tickers = List.of("TGKN", "TGKB", "IMOEX");
        final AnomalyVolumeProperties properties = createAnomalyVolumeProperties(1.5, 180, "IMOEX");
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final LocalDateTime lastExecutionDateTime = LocalDateTime.parse("2024-01-01T10:00:00");
        final Signal signal = Signal.builder()
            .id(UUID.randomUUID())
            .price(10D)
            .scannerId(scannerId)
            .ticker(tickers.get(0))
            .isOpen(true)
            .isBuy(true)
            .dateTime(lastExecutionDateTime)
            .build();
        SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .datasourceId(MOEX_DATASOURCE_ID)
            .tickers(tickers)
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
        final UUID scannerId = UUID.randomUUID();
        final List<String> tickers = List.of("TGKN", "TGKB", "IMOEX");
        final PrefCommonProperties properties = createPrefCommonProperties(1.5);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final LocalDateTime lastExecutionDateTime = LocalDateTime.parse("2024-01-01T10:00:00");
        final Signal signal = Signal.builder()
            .id(UUID.randomUUID())
            .price(10D)
            .scannerId(scannerId)
            .ticker(tickers.get(0))
            .isOpen(true)
            .isBuy(true)
            .dateTime(lastExecutionDateTime)
            .build();
        SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .datasourceId(MOEX_DATASOURCE_ID)
            .tickers(tickers)
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
        final UUID scannerId = UUID.randomUUID();
        final List<String> tickers = List.of("TGKB", "BRF4");
        final SectoralFuturesProperties properties = createSectoralFuturesProperties(0.015, 0.015, "BRF4");
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final LocalDateTime lastExecutionDateTime = LocalDateTime.parse("2024-01-01T10:00:00");
        final Signal signal = Signal.builder()
            .id(UUID.randomUUID())
            .scannerId(scannerId)
            .price(10D)
            .ticker(tickers.get(0))
            .isOpen(true)
            .isBuy(true)
            .dateTime(lastExecutionDateTime)
            .build();
        SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .datasourceId(MOEX_DATASOURCE_ID)
            .tickers(tickers)
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
        final UUID scannerId = UUID.randomUUID();
        final List<String> tickers = List.of("TGKN", "TGKB", "IMOEX");
        final SectoralRetardProperties properties = createSectoralRetardProperties(0.015, 0.015);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final LocalDateTime lastExecutionDateTime = LocalDateTime.parse("2024-01-01T10:00:00");
        final Signal signal = Signal.builder()
            .id(UUID.randomUUID())
            .scannerId(scannerId)
            .price(10D)
            .ticker(tickers.get(0))
            .isOpen(true)
            .isBuy(true)
            .dateTime(lastExecutionDateTime)
            .build();
        SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .datasourceId(MOEX_DATASOURCE_ID)
            .tickers(tickers)
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
            UUID.randomUUID(),
            MOEX_DATASOURCE_ID,
            List.of("TGKN", "TGKB", "IMOEX"),
            createAnomalyVolumeProperties(1.5, 180, "IMOEX")
        );
        final SignalScanner prefCommonNasdaqScanner = creatScanner(
            UUID.randomUUID(),
            NASDAQ_DATASOURCE_ID,
            List.of("APPL", "APPLP"),
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
        UUID scannerId,
        UUID datasourceId,
        List<String> tickers,
        AlgorithmProperties properties
    ) {
        return SignalScanner.builder()
            .id(scannerId)
            .datasourceId(datasourceId)
            .tickers(tickers)
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
        String indexTicker
    ) {
        return new AnomalyVolumeProperties(scaleCoefficient, historyPeriod, indexTicker);
    }

    private SectoralFuturesProperties createSectoralFuturesProperties(
        double futuresOvernightScale,
        double stockOvernightScale,
        String futuresTicker
    ) {
        return new SectoralFuturesProperties(futuresOvernightScale, stockOvernightScale, futuresTicker);
    }
}
