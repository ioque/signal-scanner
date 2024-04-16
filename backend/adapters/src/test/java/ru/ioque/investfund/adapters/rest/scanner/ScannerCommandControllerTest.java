package ru.ioque.investfund.adapters.rest.scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;
import ru.ioque.investfund.adapters.rest.scanner.request.AnomalyVolumePropertiesDto;
import ru.ioque.investfund.adapters.rest.scanner.request.CreateScannerRequest;
import ru.ioque.investfund.adapters.rest.scanner.request.PrefSimplePropertiesDto;
import ru.ioque.investfund.adapters.rest.scanner.request.SectoralFuturesPropertiesDto;
import ru.ioque.investfund.adapters.rest.scanner.request.SectoralRetardPropertiesDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SCANNER CONFIGURATOR COMMAND CONTROLLER TEST")
public class ScannerCommandControllerTest extends BaseControllerTest {
    private static final UUID DATASOURCE_ID = UUID.randomUUID();
    @Test
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        аномальных объемов.
        """)
    public void testCase1() {
        assertIsOk(
            CreateScannerRequest.builder()
                .description("desc")
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TGKN", "IMOEX"))
                .properties(
                    AnomalyVolumePropertiesDto.builder()
                        .historyPeriod(180)
                        .indexTicker("IMOEX")
                        .scaleCoefficient(1.5).build()
                )
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
            CreateScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .description("desc")
                .tickers(List.of("TGKN", "IMOEX"))
                .properties(
                    AnomalyVolumePropertiesDto.builder()
                        .historyPeriod(180)
                        .indexTicker("IMOEX")
                        .build()
                )
                .build(),
            List.of("Не заполнен параметр scaleCoefficient.")
        );
    }

    @Test
    @DisplayName("""
        T3. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        аномальных объемов. Не переданы параметры scaleCoefficient и indexTicker.
        """)
    public void testCase3() {
        assertValidationErrors(
            CreateScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TGKN", "IMOEX"))
                .properties(
                    AnomalyVolumePropertiesDto.builder().historyPeriod(180).build()
                )
                .build(),
            List.of("Не заполнен параметр scaleCoefficient.", "Не заполнен параметр indexTicker.")
        );
    }

    @Test
    @DisplayName("""
        T4. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        аномальных объемов. Не переданы параметры scaleCoefficient и indexTicker.
        """)
    public void testCase4() {
        assertValidationErrors(
            CreateScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TGKN", "IMOEX"))
                .properties(
                    AnomalyVolumePropertiesDto.builder()
                        .scaleCoefficient(1.5)
                        .indexTicker("IMOEX")
                        .build()
                )
                .build(),
            List.of("Не заполнен параметр historyPeriod.")
        );
    }

    @Test
    @DisplayName("""
        T5. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        дельта анализа пар преф-обычка.
        """)
    public void testCase5() {
        assertIsOk(
            CreateScannerRequest.builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("BANE", "BANEP"))
                .properties(PrefSimplePropertiesDto.builder().spreadValue(1.0).build())
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
            CreateScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("BANE", "BANEP"))
                .build(),
            List.of("Не передано описание сканера.", "Не переданы параметры алгоритма.")
        );
    }

    @Test
    @DisplayName("""
        T7. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        секторальный отстающий.
        """)
    public void testCase7() {
        assertIsOk(
            CreateScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TATN", "BANE", "ROSN", "LKOH"))
                .properties(
                    SectoralRetardPropertiesDto.builder()
                        .historyScale(0.015)
                        .intradayScale(0.012)
                        .build()
                )
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
            CreateScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("desc")
                .datasourceId(DATASOURCE_ID)
                .properties(
                    SectoralRetardPropertiesDto.builder()
                        .historyScale(0.015)
                        .build()
                )
                .build(),
            List.of("Не заполнен параметр intradayScale.", "Не передан список тикеров анализируемых инструментов.")
        );
    }

    @Test
    @DisplayName("""
        T9. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        секторальный отстающий, не передан параметр historyScale и передано пустое описание.
        """)
    public void testCase9() {
        assertValidationErrors(
            CreateScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .description("")
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TATN", "BANE", "ROSN", "LKOH"))
                .properties(
                    SectoralRetardPropertiesDto.builder()
                        .intradayScale(0.012)
                        .build()
                )
                .build(),
            List.of("Не передано описание сканера.", "Не заполнен параметр historyScale.")
        );
    }

    @Test
    @DisplayName("""
        T10. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        корреляции сектора с фьючерсом на основной товар сектора.
        """)
    public void testCase10() {
        assertIsOk(
            CreateScannerRequest.builder()
                .description("desc")
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TATN", "BANE", "ROSN", "LKOH", "BRF4"))
                .properties(
                    SectoralFuturesPropertiesDto.builder()
                        .futuresTicker("BRF4")
                        .futuresOvernightScale(0.015)
                        .stockOvernightScale(0.015)
                        .build()
                )
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
            CreateScannerRequest.builder()
                .description("desc")
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of("TATN", "BANE", "ROSN", "LKOH", "BRF4"))
                .properties(new SectoralFuturesPropertiesDto())
                .build(),
            List.of(
                "Не заполнен параметр stockOvernightScale.",
                "Не заполнен параметр futuresTicker.",
                "Не заполнен параметр futuresOvernightScale."
            )
        );
    }

    @Test
    @DisplayName("""
        T12. Выполнение запроса по эндпоинту POST /api/scanner на добавление сканера сигналов по алгоритму
        дельта анализа пар преф-обычка, передан пустой список анализируемых инструментов.
        """)
    public void testCase12() {
        assertValidationErrors(
            CreateScannerRequest
                .builder()
                .workPeriodInMinutes(1)
                .datasourceId(DATASOURCE_ID)
                .tickers(List.of())
                .description("desc")
                .properties(new PrefSimplePropertiesDto(1.0))
                .build(),
            List.of("Не передан список тикеров анализируемых инструментов.")
        );
    }

    @SneakyThrows
    private void assertValidationErrors(CreateScannerRequest request, List<String> errors) {
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
    private void assertIsOk(CreateScannerRequest request) {
        mvc
            .perform(MockMvcRequestBuilders
                .post("/api/scanner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());
    }
}
