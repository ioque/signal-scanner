package ru.ioque.investfund.adapters.rest.scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;
import ru.ioque.investfund.adapters.rest.signalscanner.request.AddAnomalyVolumeScanner;
import ru.ioque.investfund.adapters.rest.signalscanner.request.AddCorrelationSectoralScanner;
import ru.ioque.investfund.adapters.rest.signalscanner.request.AddPrefSimpleScanner;
import ru.ioque.investfund.adapters.rest.signalscanner.request.AddSectoralRetardScanner;
import ru.ioque.investfund.adapters.rest.signalscanner.request.AddSignalScannerRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SIGNAL SCANNER REST CONTROLLER, ENDPOINT POST /signal-scanner")
public class AddSignalScannerRestControllerTest extends BaseControllerTest {
    private static final UUID IMOEX_ID = UUID.randomUUID();
    private static final UUID TGKN_ID = UUID.randomUUID();
    private static final UUID TATN_ID = UUID.randomUUID();
    private static final UUID ROSN_ID = UUID.randomUUID();
    private static final UUID LKOH_ID = UUID.randomUUID();
    private static final UUID BANE_ID = UUID.randomUUID();
    private static final UUID BANEP_ID = UUID.randomUUID();
    private static final UUID BRF4_ID = UUID.randomUUID();

    @Test
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        аномальных объемов.
        """)
    public void testCase1() {
        assertIsOk(
            AddAnomalyVolumeScanner.builder()
                .description("desc")
                .ids(List.of(TGKN_ID, IMOEX_ID))
                .historyPeriod(180)
                .indexTicker("IMOEX")
                .scaleCoefficient(1.5)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T2. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        аномальных объемов. Не передан параметр scaleCoefficient.
        """)
    public void testCase2() {
        assertValidationErrors(
            AddAnomalyVolumeScanner
                .builder()
                .description("desc")
                .ids(List.of(TGKN_ID, IMOEX_ID))
                .historyPeriod(180)
                .indexTicker("IMOEX")
                .build(),
            List.of("The scaleCoefficient is required.")
        );
    }

    @Test
    @DisplayName("""
        T3. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        аномальных объемов. Не переданы параметры scaleCoefficient и indexTicker.
        """)
    public void testCase3() {
        assertValidationErrors(
            AddAnomalyVolumeScanner
                .builder()
                .description("desc")
                .ids(List.of(TGKN_ID, IMOEX_ID))
                .historyPeriod(180)
                .build(),
            List.of("The scaleCoefficient is required.", "The indexTicker is required.")
        );
    }

    @Test
    @DisplayName("""
        T4. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        аномальных объемов. Не переданы параметры scaleCoefficient и indexTicker.
        """)
    public void testCase4() {
        assertValidationErrors(
            AddAnomalyVolumeScanner
                .builder()
                .description("desc")
                .ids(List.of(TGKN_ID, IMOEX_ID))
                .scaleCoefficient(1.5)
                .indexTicker("IMOEX")
                .build(),
            List.of("The historyPeriod is required.")
        );
    }

    @Test
    @DisplayName("""
        T5. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        дельта анализа пар преф-обычка.
        """)
    public void testCase5() {
        assertIsOk(
            AddPrefSimpleScanner.builder()
                .description("desc")
                .ids(List.of(BANE_ID, BANEP_ID))
                .spreadParam(1.0)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T6. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        дельта анализа пар преф-обычка, не передан параметр spreadParam и описание.
        """)
    public void testCase6() {
        assertValidationErrors(
            AddPrefSimpleScanner
                .builder()
                .ids(List.of(BANE_ID, BANEP_ID))
                .build(),
            List.of("The spreadParam is required.", "The description is required.")
        );
    }

    @Test
    @DisplayName("""
        T7. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        секторальный отстающий.
        """)
    public void testCase7() {
        assertIsOk(
            AddSectoralRetardScanner
                .builder()
                .description("desc")
                .ids(List.of(TATN_ID, BANE_ID, ROSN_ID, LKOH_ID))
                .historyScale(0.015)
                .intradayScale(0.012)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T8. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        секторальный отстающий, не передан список объектов и параметр intradayScale.
        """)
    public void testCase8() {
        assertValidationErrors(
            AddSectoralRetardScanner
                .builder()
                .description("desc")
                .historyScale(0.015)
                .build(),
            List.of("The ids is required.", "The intradayScale is required.")
        );
    }

    @Test
    @DisplayName("""
        T9. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        секторальный отстающий, не передан параметр historyScale и передано пустое описание.
        """)
    public void testCase9() {
        assertValidationErrors(
            AddSectoralRetardScanner
                .builder()
                .description("")
                .ids(List.of(TATN_ID, BANE_ID, ROSN_ID, LKOH_ID))
                .intradayScale(0.015)
                .build(),
            List.of("The historyScale is required.", "The description is required.")
        );
    }

    @Test
    @DisplayName("""
        T10. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        корреляции сектора с фьючерсом на основной товар сектора.
        """)
    public void testCase10() {
        assertIsOk(
            AddCorrelationSectoralScanner.builder()
                .description("desc")
                .ids(List.of(BANE_ID, TATN_ID, LKOH_ID, ROSN_ID, BRF4_ID))
                .futuresTicker("BRF4")
                .futuresOvernightScale(0.015)
                .stockOvernightScale(0.015)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T11. Выполнение запроса по эндпоинту POST /api/v1/signal-scanner на добавление сканера сигналов по алгоритму
        корреляции сектора с фьючерсом на основной товар сектора, не переданы параметры.
        """)
    public void testCase11() {
        assertValidationErrors(
            AddCorrelationSectoralScanner.builder()
                .description("desc")
                .ids(List.of(BANE_ID, TATN_ID, LKOH_ID, ROSN_ID, BRF4_ID))
                .build(),
            List.of(
                "The futuresTicker is required.",
                "The futuresOvernightScale is required.",
                "The stockOvernightScale is required."
            )
        );
    }

    @SneakyThrows
    private void assertValidationErrors(AddSignalScannerRequest request, List<String> errors) {
        mvc
            .perform(MockMvcRequestBuilders
                .post("/api/v1/signal-scanner")
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
    private void assertIsOk(AddSignalScannerRequest request) {
        mvc
            .perform(MockMvcRequestBuilders
                .post("/api/v1/signal-scanner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());
    }
}
