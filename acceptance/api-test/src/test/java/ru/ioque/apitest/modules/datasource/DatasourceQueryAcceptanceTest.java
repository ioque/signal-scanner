package ru.ioque.apitest.modules.datasource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.apitest.DatasourceEmulatedTest;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dataset.DefaultInstrumentSet;
import ru.ioque.core.dto.datasource.request.DatasourceRequest;
import ru.ioque.core.dto.datasource.response.InstrumentResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("DATASOURCE QUERY ACCEPTANCE TEST")
public class DatasourceQueryAcceptanceTest extends DatasourceEmulatedTest {
    @BeforeEach
    void initDateTime() {
        initDateTime(LocalDateTime.parse("2024-04-22T13:00:00"));
        createDatasource(
            DatasourceRequest.builder()
                .name("Московская Биржа")
                .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
                .url(datasourceHost)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T1. Поиск финансовых инструментов по тикеру.
        """)
    void testCase3() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(2, getInstruments(datasourceId, Map.of("ticker", "SBER")).size());
    }

    @Test
    @DisplayName("""
        T2. Поиск финансовых инструментов по типу.
        """)
    void testCase4() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(2, getInstruments(datasourceId, Map.of("type", "stock")).size());
        assertEquals(1, getInstruments(datasourceId, Map.of("type", "currencyPair")).size());
        assertEquals(1, getInstruments(datasourceId, Map.of("type", "futures")).size());
        assertEquals(1, getInstruments(datasourceId, Map.of("type", "index")).size());
    }

    @Test
    @DisplayName("""
        T3. Поиск финансовых инструментов по названию.
        """)
    void testCase5() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(2, getInstruments(datasourceId, Map.of("shortname", "Сбер")).size());
    }

    @Test
    @DisplayName("""
        T4. Поиск финансовых инструментов по названию и типу.
        """)
    void testCase6() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(1, getInstruments(datasourceId, Map.of("shortname", "BR", "type", "futures")).size());
    }

    @Test
    @DisplayName("""
        T5. Поиск финансовых инструментов по тикеру и типу.
        """)
    void testCase7() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(1, getInstruments(datasourceId, Map.of("ticker", "IMOEX", "type", "index")).size());
    }

    @Test
    @DisplayName("""
        T6. Поиск финансовых инструментов по тикеру, названию и типу.
        """)
    void testCase8() {
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(
                        DefaultInstrumentSet.imoex(),
                        DefaultInstrumentSet.usbRub(),
                        DefaultInstrumentSet.brf4(),
                        DefaultInstrumentSet.sber(),
                        DefaultInstrumentSet.sberp()
                    )
                )
                .build()
        );
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        assertEquals(
            2,
            getInstruments(datasourceId, Map.of("shortname", "Сбер", "ticker", "SBER", "type", "stock")).size()
        );
    }

    @Test
    @DisplayName("""
        T7. Получение детализированной информации по финансовому инструменту.
        """)
    void testCase9() {
        initDataset(Dataset.builder().instruments(List.of(DefaultInstrumentSet.sber())).build());
        UUID datasourceId = getAllDatasource().get(0).getId();

        integrateInstruments(datasourceId);

        InstrumentResponse instrumentResponse = getInstrumentBy(datasourceId, "SBER");

        assertEquals("SBER", instrumentResponse.getTicker());
        assertEquals("Сбербанк", instrumentResponse.getShortName());
        assertEquals("ПАО Сбербанк", instrumentResponse.getName());
    }
}
