package ru.ioque.investfund.scanner.configurator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.Ticker;
import ru.ioque.investfund.domain.scanner.command.CreateScannerCommand;
import ru.ioque.investfund.domain.scanner.command.UpdateScannerCommand;
import ru.ioque.investfund.domain.scanner.algorithms.properties.SectoralFuturesProperties;

import java.util.List;

@DisplayName("SCANNER MANAGER TEST - SAVE SECTORAL CORRELATION SCANNER")
public class SectoralFuturesConfiguratorTest extends BaseConfiguratorTest {
    @Test
    @DisplayName("""
        T1. В команду на создание CorrelationSectoralScanner не передан параметр futuresOvernightScale.
        """)
    void testCase1() {
        testAddNewScannerError(
            buildCreateSectoralFuturesScannerWith()
                .properties(buildPropertiesWith().futuresOvernightScale(null).build())
                .build(),
            futuresOvernightScaleIsEmpty()
        );
    }

    private SectoralFuturesProperties.SectoralFuturesPropertiesBuilder buildPropertiesWith() {
        return SectoralFuturesProperties.builder()
            .futuresId(brf4Id)
            .futuresOvernightScale(0.05)
            .stockOvernightScale(0.05);
    }

    @Test
    @DisplayName("""
        T2. В команду на создание CorrelationSectoralScanner не передан параметр stockOvernightScale.
        """)
    void testCase2() {
        testAddNewScannerError(
            buildCreateSectoralFuturesScannerWith()
                .properties(buildPropertiesWith().stockOvernightScale(null).build())
                .build(),
            stockOvernightScaleIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T3. В команду на создание CorrelationSectoralScanner не передан параметр futuresTicker.
        """)
    void testCase3() {
        testAddNewScannerError(
            buildCreateSectoralFuturesScannerWith()
                .properties(buildPropertiesWith().futuresId(null).build())
                .build(),
            futuresIdIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T4. В команду на создание CorrelationSectoralScanner параметр futuresTicker передан как пустая строка.
        """)
    void testCase4() {
        testAddNewScannerError(
            buildCreateSectoralFuturesScannerWith()
                .properties(buildPropertiesWith().futuresId(new InstrumentId(new Ticker(""))).build())
                .build(),
            futuresIdIsNotValid()
        );
    }

    @Test
    @DisplayName("""
        T5. В команду на создание CorrelationSectoralScanner параметр futuresOvernightScale передан со значением = 0.
        """)
    void testCase5() {
        testAddNewScannerError(
            buildCreateSectoralFuturesScannerWith()
                .properties(buildPropertiesWith().futuresOvernightScale(0D).build())
                .build(),
            futuresOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T6. В команду на создание CorrelationSectoralScanner параметр futuresOvernightScale передан со значением < 0.
        """)
    void testCase6() {
        testAddNewScannerError(
            buildCreateSectoralFuturesScannerWith()
                .properties(buildPropertiesWith().futuresOvernightScale(-1D).build())
                .build(),
            futuresOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T7. В команду на создание CorrelationSectoralScanner параметр stockOvernightScale передан со значением = 0.
        """)
    void testCase7() {
        testAddNewScannerError(
            buildCreateSectoralFuturesScannerWith()
                .properties(buildPropertiesWith().stockOvernightScale(-1D).build())
                .build(),
            stockOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T8. В команду на создание CorrelationSectoralScanner параметр stockOvernightScale передан со значением < 0.
        """)
    void testCase8() {
        testAddNewScannerError(
            buildCreateSectoralFuturesScannerWith()
                .properties(buildPropertiesWith().stockOvernightScale(-1D).build())
                .build(),
            stockOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T9. В команду на обновление CorrelationSectoralScanner не передан параметр futuresOvernightScale.
        """)
    void testCase9() {
        commandBus().execute(buildCreateSectoralFuturesScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralFuturesScannerWith()
                .scannerId(getFirstScannerId())
                .properties(
                    buildPropertiesWith().futuresOvernightScale(null).build()
                )
                .build(),
            futuresOvernightScaleIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T10. В команду на обновление CorrelationSectoralScanner не передан параметр stockOvernightScale.
        """)
    void testCase10() {
        commandBus().execute(buildCreateSectoralFuturesScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralFuturesScannerWith()
                .scannerId(getFirstScannerId())
                .properties(
                    buildPropertiesWith().stockOvernightScale(null).build()
                )
                .build(),
            stockOvernightScaleIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T11. В команду на обновление CorrelationSectoralScanner не передан параметр futuresTicker.
        """)
    void testCase11() {
        commandBus().execute(buildCreateSectoralFuturesScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralFuturesScannerWith()
                .scannerId(getFirstScannerId())
                .properties(
                    buildPropertiesWith().futuresId(null).build()
                )
                .build(),
            futuresIdIsEmpty()
        );
    }

    @Test
    @DisplayName("""
        T12. В команду на обновление CorrelationSectoralScanner параметр futuresTicker передан как пустая строка.
        """)
    void testCase12() {
        commandBus().execute(buildCreateSectoralFuturesScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralFuturesScannerWith()
                .scannerId(getFirstScannerId())
                .properties(
                    buildPropertiesWith().futuresId(new InstrumentId(new Ticker(""))).build()
                )
                .build(),
            futuresIdIsNotValid()
        );
    }

    @Test
    @DisplayName("""
        T13. В команду на обновление CorrelationSectoralScanner параметр futuresOvernightScale передан со значением = 0.
        """)
    void testCase13() {
        commandBus().execute(buildCreateSectoralFuturesScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralFuturesScannerWith()
                .scannerId(getFirstScannerId())
                .properties(
                    buildPropertiesWith().futuresOvernightScale(0D).build()
                )
                .build(),
            futuresOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T14. В команду на обновление CorrelationSectoralScanner параметр futuresOvernightScale передан со значением < 0.
        """)
    void testCase14() {
        commandBus().execute(buildCreateSectoralFuturesScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralFuturesScannerWith()
                .scannerId(getFirstScannerId())
                .properties(
                    buildPropertiesWith().futuresOvernightScale(-1D).build()
                )
                .build(),
            futuresOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T15. В команду на обновление CorrelationSectoralScanner параметр stockOvernightScale передан со значением = 0.
        """)
    void testCase15() {
        commandBus().execute(buildCreateSectoralFuturesScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralFuturesScannerWith()
                .scannerId(getFirstScannerId())
                .properties(
                    buildPropertiesWith().stockOvernightScale(0D).build()
                )
                .build(),
            stockOvernightScaleIsNegative()
        );
    }

    @Test
    @DisplayName("""
        T16. В команду на обновление CorrelationSectoralScanner параметр stockOvernightScale передан со значением < 0.
        """)
    void testCase16() {
        commandBus().execute(buildCreateSectoralFuturesScannerWith().build());
        testUpdateScannerError(
            buildUpdateSectoralFuturesScannerWith()
                .scannerId(getFirstScannerId())
                .properties(
                    buildPropertiesWith().stockOvernightScale(-1D).build()
                )
                .build(),
            stockOvernightScaleIsNegative()
        );
    }

    private String futuresOvernightScaleIsEmpty() {
        return "Не передан параметр futuresOvernightScale.";
    }
    private String stockOvernightScaleIsEmpty() {
        return "Не передан параметр stockOvernightScale.";
    }

    private String futuresIdIsEmpty() {
        return "Не передан идентификатор фьючерса на основной товар сектора.";
    }

    private String futuresIdIsNotValid() {
        return "Идентификатор должен быть непустой строкой, состоящей из латинских букв или цифр.";
    }

    private String stockOvernightScaleIsNegative() {
        return "Параметр stockOvernightScale должен быть больше 0.";
    }

    private String futuresOvernightScaleIsNegative() {
        return "Параметр futuresOvernightScale должен быть больше 0.";
    }

    protected CreateScannerCommand.CreateScannerCommandBuilder buildCreateSectoralFuturesScannerWith() {
        return CreateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .datasourceId(getDatasourceId())
            .instrumentIds(List.of(tgknId, tgkbId, imoexId))
            .properties(
                SectoralFuturesProperties.builder()
                    .futuresOvernightScale(0.015)
                    .stockOvernightScale(0.015)
                    .futuresId(brf4Id)
                    .build()
            );
    }

    protected UpdateScannerCommand.UpdateScannerCommandBuilder buildUpdateSectoralFuturesScannerWith() {
        return UpdateScannerCommand.builder()
            .workPeriodInMinutes(1)
            .description("description")
            .scannerId(getFirstScannerId())
            .instrumentIds(List.of(tgknId, tgkbId, imoexId))
            .properties(
                SectoralFuturesProperties.builder()
                    .futuresOvernightScale(0.015)
                    .stockOvernightScale(0.015)
                    .futuresId(brf4Id)
                    .build()
            );
    }
}
