package ru.ioque.investfund.adapters.integration.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.psql.PsqlScannerRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.ScannerStatus;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AlgorithmProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.AnomalyVolumeProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.PrefCommonProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralRetardProperties;

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
        initializeDatasource();
    }

    @Test
    @DisplayName("""
        T1. Сохранение сканера аномальных объемов
        """)
    void testCase1() {
        final ScannerId scannerId = ScannerId.from(UUID.randomUUID());
        final List<InstrumentId> instrumentIds = List.of(TGKN_ID, TGKB_ID, IMOEX_ID);
        final AnomalyVolumeProperties properties = createAnomalyVolumeProperties(1.5, 180, IMOEX);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .status(ScannerStatus.ACTIVE)
            .datasourceId(MOEX_DATASOURCE_ID)
            .instrumentIds(instrumentIds)
            .properties(properties)
            .description(desc)
            .workPeriodInMinutes(workPeriodInMinutes)
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
        final SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .status(ScannerStatus.ACTIVE)
            .datasourceId(MOEX_DATASOURCE_ID)
            .instrumentIds(instrumentIds)
            .properties(properties)
            .description(desc)
            .workPeriodInMinutes(workPeriodInMinutes)
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
        final List<InstrumentId> instrumentIds = List.of(TGKB_ID, BRF4_ID);
        final SectoralFuturesProperties properties = createSectoralFuturesProperties(0.015, 0.015, BRF4);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .status(ScannerStatus.ACTIVE)
            .datasourceId(MOEX_DATASOURCE_ID)
            .instrumentIds(instrumentIds)
            .properties(properties)
            .description(desc)
            .workPeriodInMinutes(workPeriodInMinutes)
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
        final List<InstrumentId> instrumentIds = List.of(TGKN_ID, TGKB_ID, IMOEX_ID);
        final SectoralRetardProperties properties = createSectoralRetardProperties(0.015, 0.015);
        final String desc = "description";
        final Integer workPeriodInMinutes = 1;
        final SignalScanner scanner = SignalScanner.builder()
            .id(scannerId)
            .status(ScannerStatus.ACTIVE)
            .datasourceId(MOEX_DATASOURCE_ID)
            .instrumentIds(instrumentIds)
            .properties(properties)
            .description(desc)
            .workPeriodInMinutes(workPeriodInMinutes)
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
            createAnomalyVolumeProperties(1.5, 180, IMOEX)
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
            .status(ScannerStatus.ACTIVE)
            .datasourceId(datasourceId)
            .instrumentIds(instrumentIds)
            .properties(properties)
            .description("desc")
            .workPeriodInMinutes(1)
            .build();
    }

    private PrefCommonProperties createPrefCommonProperties(double spreadValue) {
        return new PrefCommonProperties(spreadValue);
    }

    private AnomalyVolumeProperties createAnomalyVolumeProperties(
        double scaleCoefficient,
        int historyPeriod,
        Ticker indexTicker
    ) {
        return new AnomalyVolumeProperties(scaleCoefficient, historyPeriod, indexTicker);
    }

    private SectoralFuturesProperties createSectoralFuturesProperties(
        double futuresOvernightScale,
        double stockOvernightScale,
        Ticker futuresTicker
    ) {
        return new SectoralFuturesProperties(futuresOvernightScale, stockOvernightScale, futuresTicker);
    }
}
