package ru.ioque.apitest.modules.datasource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.instrument.Stock;
import ru.ioque.core.dataset.DefaultInstrumentSet;
import ru.ioque.core.dto.datasource.request.DatasourceRequest;
import ru.ioque.core.dto.datasource.response.InstrumentInListResponse;
import ru.ioque.core.dto.datasource.response.InstrumentResponse;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("INSTRUMENT INTEGRATION ACCEPTANCE TEST")
public class InstrumentIntegrationAcceptanceTest extends DatasourceAcceptanceTest {
    @BeforeEach
    void initDateTime() {
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
        T1. Интеграция инструментов.
        """)
    void testCase1() {
        final UUID datasourceId = getFirstDatasourceId();
        final List<Instrument> instrumentList = List.of(
            DefaultInstrumentSet.imoex(),
            DefaultInstrumentSet.usbRub(),
            DefaultInstrumentSet.brf4(),
            DefaultInstrumentSet.sber()
        );
        initInstruments(instrumentList);

        integrateInstruments(datasourceId);

        final List<InstrumentInListResponse> instruments = getInstruments(datasourceId);
        assertEquals(4, instruments.size());
        instrumentList.forEach(dto -> {
            InstrumentResponse instrumentResponse = getInstrumentBy(datasourceId, dto.getTicker());
            assertEquals(dto.getTicker(), instrumentResponse.getTicker());
            assertEquals(dto.getName(), instrumentResponse.getName());
            assertEquals(dto.getShortName(), instrumentResponse.getShortName());
            assertFalse(instrumentResponse.getUpdatable());
        });
    }

    @Test
    @DisplayName("""
        T2. Один и тот же список инструментов интегрируется дважды.
        """)
    void testCase2() {
        final UUID datasourceId = getFirstDatasourceId();
        final List<Instrument> instrumentList = List.of(
            DefaultInstrumentSet.imoex(),
            DefaultInstrumentSet.usbRub(),
            DefaultInstrumentSet.brf4(),
            DefaultInstrumentSet.sber()
        );
        initInstruments(instrumentList);

        integrateInstruments(datasourceId);
        integrateInstruments(datasourceId);

        final List<InstrumentInListResponse> instruments = getInstruments(datasourceId);
        assertEquals(instrumentList.size(), instruments.size());
    }

    @Test
    @DisplayName("""
        T3. Инструмент интегрируется дважды. При повторной интеграции изменились некоторые поля этого инструмента.
        """)
    void testCase3() {
        final UUID datasourceId = getFirstDatasourceId();
        final String ticker = "SBER";
        final String expectedName = "ПАО Сбербанк 1";
        final String expectedShortName = "Сбербанк 1";

        integrateInstruments(
            datasourceId,
            Stock.builder()
                .ticker(ticker)
                .lotSize(100)
                .name("ПАО Сбербанк")
                .shortName("Сбербанк")
                .build()
        );
        integrateInstruments(
            datasourceId,
            Stock.builder()
                .ticker(ticker)
                .lotSize(100)
                .name(expectedName)
                .shortName(expectedShortName)
                .build()
        );

        final InstrumentResponse instrumentResponse = getInstrumentBy(datasourceId, ticker);
        assertEquals(expectedName, instrumentResponse.getName());
        assertEquals(expectedShortName, instrumentResponse.getShortName());
    }
}
