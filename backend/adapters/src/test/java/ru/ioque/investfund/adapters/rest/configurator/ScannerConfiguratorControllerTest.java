package ru.ioque.investfund.adapters.rest.configurator;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;
import ru.ioque.investfund.adapters.rest.configurator.request.AnomalyVolumeScannerRequest;
import ru.ioque.investfund.adapters.rest.configurator.request.CorrelationSectoralScannerRequest;
import ru.ioque.investfund.adapters.rest.configurator.request.PrefSimpleScannerRequest;
import ru.ioque.investfund.adapters.rest.configurator.request.ScannerRequest;
import ru.ioque.investfund.adapters.rest.configurator.request.SectoralRetardScannerRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SCANNER CONFIGURATOR CONTROLLER TEST")
public class ScannerConfiguratorControllerTest extends BaseControllerTest {
    private static final UUID DATASOURCE_ID = UUID.randomUUID();
    @Test
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        аномальных объемов.
        """)
    public void testCase1() {
        assertIsOk(
            AnomalyVolumeScannerRequest.builder()
                .description("desc")
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TGKN", "IMOEX"))
                .historyPeriod(180)
                .indexTicker("IMOEX")
                .scaleCoefficient(1.5)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T2. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        аномальных объемов. Не передан параметр scaleCoefficient.
        """)
    public void testCase2() {
        assertValidationErrors(
            AnomalyVolumeScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .description("desc")
                .tickers(List.of("TGKN", "IMOEX"))
                .historyPeriod(180)
                .indexTicker("IMOEX")
                .build(),
            List.of("The scaleCoefficient is required.")
        );
    }

    @Test
    @DisplayName("""
        T3. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        аномальных объемов. Не переданы параметры scaleCoefficient и indexTicker.
        """)
    public void testCase3() {
        assertValidationErrors(
            AnomalyVolumeScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TGKN", "IMOEX"))
                .historyPeriod(180)
                .build(),
            List.of("The scaleCoefficient is required.", "The indexTicker is required.")
        );
    }

    @Test
    @DisplayName("""
        T4. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        аномальных объемов. Не переданы параметры scaleCoefficient и indexTicker.
        """)
    public void testCase4() {
        assertValidationErrors(
            AnomalyVolumeScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TGKN", "IMOEX"))
                .scaleCoefficient(1.5)
                .indexTicker("IMOEX")
                .build(),
            List.of("The historyPeriod is required.")
        );
    }

    @Test
    @DisplayName("""
        T5. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        дельта анализа пар преф-обычка.
        """)
    public void testCase5() {
        assertIsOk(
            PrefSimpleScannerRequest.builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("BANE", "BANEP"))
                .spreadParam(1.0)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T6. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        дельта анализа пар преф-обычка, не передан параметр spreadParam и описание.
        """)
    public void testCase6() {
        assertValidationErrors(
            PrefSimpleScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("BANE", "BANEP"))
                .build(),
            List.of("The spreadParam is required.", "The description is required.")
        );
    }

    @Test
    @DisplayName("""
        T7. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        секторальный отстающий.
        """)
    public void testCase7() {
        assertIsOk(
            SectoralRetardScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TATN", "BANE", "ROSN", "LKOH"))
                .historyScale(0.015)
                .intradayScale(0.012)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T8. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        секторальный отстающий, не передан список объектов и параметр intradayScale.
        """)
    public void testCase8() {
        assertValidationErrors(
            SectoralRetardScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .historyScale(0.015)
                .build(),
            List.of("The tickers is required.", "The intradayScale is required.")
        );
    }

    @Test
    @DisplayName("""
        T9. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        секторальный отстающий, не передан параметр historyScale и передано пустое описание.
        """)
    public void testCase9() {
        assertValidationErrors(
            SectoralRetardScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TATN", "BANE", "ROSN", "LKOH"))
                .intradayScale(0.015)
                .build(),
            List.of("The historyScale is required.", "The description is required.")
        );
    }

    @Test
    @DisplayName("""
        T10. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        корреляции сектора с фьючерсом на основной товар сектора.
        """)
    public void testCase10() {
        assertIsOk(
            CorrelationSectoralScannerRequest.builder()
                .description("desc")
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TATN", "BANE", "ROSN", "LKOH", "BRF4"))
                .futuresTicker("BRF4")
                .futuresOvernightScale(0.015)
                .stockOvernightScale(0.015)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T11. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        корреляции сектора с фьючерсом на основной товар сектора, не переданы параметры.
        """)
    public void testCase11() {
        assertValidationErrors(
            CorrelationSectoralScannerRequest.builder()
                .description("desc")
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TATN", "BANE", "ROSN", "LKOH", "BRF4"))
                .build(),
            List.of(
                "The futuresTicker is required.",
                "The futuresOvernightScale is required.",
                "The stockOvernightScale is required."
            )
        );
    }

    @SneakyThrows
    private void assertValidationErrors(ScannerRequest request, List<String> errors) {
        mvc
            .perform(MockMvcRequestBuilders
                .post("/api/scanner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(Map.of("errors", errors))
                    )
            );
    }

    @SneakyThrows
    private void assertIsOk(ScannerRequest request) {
        mvc
            .perform(MockMvcRequestBuilders
                .post("/api/scanner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());
    }
}
