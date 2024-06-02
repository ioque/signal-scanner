package ru.ioque.investfund.adapters.integration.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.ioque.investfund.adapters.integration.InfrastructureTest;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.AnomalyVolumeScannerEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.PrefSimpleScannerEntity;
import ru.ioque.investfund.adapters.psql.dao.JpaDatasourceRepository;
import ru.ioque.investfund.adapters.psql.dao.JpaScannerRepository;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("PSQL EMULATED POSITION QUERY SERVICE TEST")
public class PsqlPositionQueryServiceTest extends InfrastructureTest {

    private static final UUID SCANNER_ID_1 = UUID.randomUUID();
    private static final UUID SCANNER_ID_2 = UUID.randomUUID();

    @Autowired
    private PsqlEmulatedPositionQueryService psqlEmulatedPositionQueryService;
    @Autowired
    private JpaDatasourceRepository datasourceRepository;
    @Autowired
    private JpaScannerRepository scannerRepository;
    @Autowired
    private JpaEmulatedPositionRepository emulatedPositionRepository;

    @BeforeEach
    void beforeEach() {
        emulatedPositionRepository.deleteAll();
        scannerRepository.deleteAll();
        datasourceRepository.deleteAll();
    }

    @Test
    @DisplayName("""
        T1. Фильтрация по тикеру
        """)
    void testCase1() {
        prepareState();
        final EmulatedPositionFilterParams params = EmulatedPositionFilterParams.builder()
            .ticker("TGKN")
            .build();
        List<EmulatedPositionEntity> page = psqlEmulatedPositionQueryService.findEmulatedPositions(params);
        assertEquals(1, page.size());
    }

    @Test
    @DisplayName("""
        T2. Фильтрация по идентификатору сканера
        """)
    void testCase2() {
        prepareState();
        final EmulatedPositionFilterParams params = EmulatedPositionFilterParams.builder()
            .scannerId(SCANNER_ID_1)
            .build();
        List<EmulatedPositionEntity> page = psqlEmulatedPositionQueryService.findEmulatedPositions(params);
        assertEquals(4, page.size());
    }

    @Test
    @DisplayName("""
        T3. Фильтрация по тикеру и идентификатору сканера
        """)
    void testCase3() {
        prepareState();
        final EmulatedPositionFilterParams params = EmulatedPositionFilterParams.builder()
            .ticker("SBERP")
            .scannerId(SCANNER_ID_2)
            .build();
        List<EmulatedPositionEntity> page = psqlEmulatedPositionQueryService.findEmulatedPositions(params);
        assertEquals(1, page.size());
    }

    @Test
    @DisplayName("""
        T4. Постраничное получение данных
        """)
    void testCase4() {
        prepareState();
        List<EmulatedPositionEntity> firstPage = psqlEmulatedPositionQueryService.findEmulatedPositions(
            PageRequest.of(0, 3)
        );
        List<EmulatedPositionEntity> secondPage = psqlEmulatedPositionQueryService.findEmulatedPositions(
            PageRequest.of(1, 3)
        );
        List<EmulatedPositionEntity> thirdPage = psqlEmulatedPositionQueryService.findEmulatedPositions(
            PageRequest.of(2, 3)
        );

        assertEquals(3, firstPage.size());
        assertEquals(2, secondPage.size());
        assertEquals(0, thirdPage.size());
    }

    @Test
    @DisplayName("""
        T5. Сортировка по прибыльности
        """)
    void testCase5() {
        prepareState();
        final EmulatedPositionFilterParams params = EmulatedPositionFilterParams.builder()
            .pageNumber(0)
            .pageSize(5)
            .orderDirection("DESC")
            .orderField("profit")
            .build();
        List<EmulatedPositionEntity> page = psqlEmulatedPositionQueryService.findEmulatedPositions(params);
        assertEquals(5, page.size());
        assertEquals(30D, page.get(0).getProfit());
        assertEquals(-10D, page.get(4).getProfit());
    }

    @Test
    @DisplayName("""
        T6. Сортировка по цене открытия
        """)
    void testCase6() {
        prepareState();
        final EmulatedPositionFilterParams params = EmulatedPositionFilterParams.builder()
            .pageNumber(0)
            .pageSize(5)
            .orderDirection("DESC")
            .orderField("openPrice")
            .build();
        List<EmulatedPositionEntity> page = psqlEmulatedPositionQueryService.findEmulatedPositions(params);
        assertEquals(5, page.size());
        assertEquals(250D, page.get(0).getOpenPrice());
        assertEquals(110D, page.get(4).getOpenPrice());
    }

    @Test
    @DisplayName("""
        T7. Сортировка по последней цене
        """)
    void testCase7() {
        prepareState();
        final EmulatedPositionFilterParams params = EmulatedPositionFilterParams.builder()
            .pageNumber(0)
            .pageSize(5)
            .orderDirection("DESC")
            .orderField("lastPrice")
            .build();
        List<EmulatedPositionEntity> page = psqlEmulatedPositionQueryService.findEmulatedPositions(params);
        assertEquals(5, page.size());
        assertEquals(250D, page.get(0).getLastPrice());
        assertEquals(130D, page.get(4).getLastPrice());
    }

    @Test
    @DisplayName("""
        T8. Сортировка по цене закрытия
        """)
    void testCase8() {
        prepareState();
        final EmulatedPositionFilterParams params = EmulatedPositionFilterParams.builder()
            .pageNumber(0)
            .pageSize(5)
            .orderDirection("DESC")
            .orderField("closePrice")
            .build();
        List<EmulatedPositionEntity> page = psqlEmulatedPositionQueryService.findEmulatedPositions(params);
        assertEquals(5, page.size());
        assertEquals(130, page.get(0).getClosePrice());
        assertNull(page.get(4).getClosePrice());
    }

    private void prepareState() {
        final UUID datasourceId = UUID.randomUUID();
        final InstrumentEntity tgkn = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .ticker("TGKN")
            .type(InstrumentType.STOCK)
            .build();
        final InstrumentEntity tgkb = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .ticker("TGKB")
            .build();
        final InstrumentEntity tgkr = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .ticker("TGKR")
            .type(InstrumentType.STOCK)
            .build();
        final InstrumentEntity tgkt = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .ticker("TGKT")
            .type(InstrumentType.STOCK)
            .build();
        final InstrumentEntity sber = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .ticker("SBER")
            .type(InstrumentType.STOCK)
            .build();
        final InstrumentEntity sberp = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .ticker("SBERP")
            .type(InstrumentType.STOCK)
            .build();
        final InstrumentEntity imoex = InstrumentEntity.builder()
            .id(UUID.randomUUID())
            .ticker("IMOEX")
            .type(InstrumentType.INDEX)
            .build();
        final AnomalyVolumeScannerEntity anomalyVolumeScanner = AnomalyVolumeScannerEntity.builder()
            .id(SCANNER_ID_1)
            .description("desc")
            .datasourceId(datasourceId)
            .instrumentIds(
                List.of(
                    tgkn.getId(),
                    tgkb.getId(),
                    tgkr.getId(),
                    tgkt.getId(),
                    imoex.getId()
                )
            )
            .historyPeriod(100)
            .indexTicker("IMOEX")
            .scaleCoefficient(1.5)
            .workPeriodInMinutes(1)
            .build();
        final PrefSimpleScannerEntity prefSimpleScanner = PrefSimpleScannerEntity.builder()
            .id(SCANNER_ID_2)
            .description("desc")
            .datasourceId(datasourceId)
            .instrumentIds(List.of(
                sber.getId(), sberp.getId()
            ))
            .spreadParam(1.0)
            .workPeriodInMinutes(1)
            .build();
        datasourceRepository.save(
            DatasourceEntity.builder()
                .id(datasourceId)
                .url("http://datasource.ru")
                .name("datasource")
                .description("datasource")
                .instruments(Set.of(tgkn, tgkb, tgkr, tgkt, sber, sberp, imoex))
                .build()
        );
        scannerRepository.save(anomalyVolumeScanner);
        scannerRepository.save(prefSimpleScanner);
        emulatedPositionRepository.saveAll(
            List.of(
                EmulatedPositionEntity.builder()
                    .id(UUID.randomUUID())
                    .instrument(sberp)
                    .openPrice(250D)
                    .lastPrice(250D)
                    .profit(0D)
                    .isOpen(true)
                    .scanner(prefSimpleScanner)
                    .build(),
                EmulatedPositionEntity.builder()
                    .id(UUID.randomUUID())
                    .instrument(tgkn)
                    .openPrice(110D)
                    .lastPrice(140D)
                    .profit(30D)
                    .isOpen(true)
                    .scanner(anomalyVolumeScanner)
                    .build(),
                EmulatedPositionEntity.builder()
                    .id(UUID.randomUUID())
                    .instrument(tgkb)
                    .openPrice(120D)
                    .lastPrice(140D)
                    .profit(20D)
                    .isOpen(true)
                    .scanner(anomalyVolumeScanner)
                    .build(),
                EmulatedPositionEntity.builder()
                    .id(UUID.randomUUID())
                    .instrument(tgkr)
                    .openPrice(130D)
                    .lastPrice(155D)
                    .profit(25D)
                    .isOpen(true)
                    .scanner(anomalyVolumeScanner)
                    .build(),
                EmulatedPositionEntity.builder()
                    .id(UUID.randomUUID())
                    .instrument(tgkt)
                    .openPrice(140D)
                    .closePrice(130D)
                    .lastPrice(130D)
                    .profit(-10D)
                    .isOpen(false)
                    .scanner(anomalyVolumeScanner)
                    .build()
            )
        );
    }
}
