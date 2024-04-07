package ru.ioque.apitest.perfomance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.ioque.apitest.api.BaseApiAcceptanceTest;
import ru.ioque.core.datagenerator.config.DealsGeneratorConfig;
import ru.ioque.core.datagenerator.core.HistoryGeneratorConfig;
import ru.ioque.core.datagenerator.core.PercentageGrowths;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dataset.DefaultInstrumentSet;
import ru.ioque.core.dto.exchange.request.RegisterDatasourceRequest;
import ru.ioque.core.dto.exchange.response.InstrumentResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PERFORMANCE")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class PerformanceAcceptanceTest extends BaseApiAcceptanceTest {
    @BeforeEach
    void initDateTime() {
        registerDatasource(
            RegisterDatasourceRequest.builder()
                .name("Московская Биржа")
                .description("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.")
                .url(datasourceHost)
                .build()
        );
    }

    @Test
    @DisplayName("""
        T1. Загрузка 50к сделок по торгам внутри дня
        """)
    void testCase1() {
        LocalDateTime time = LocalDateTime.now();
        LocalDate startDate = time.toLocalDate().minusMonths(6);
        initDataset(
            Dataset.builder()
                .instruments(
                    List.of(DefaultInstrumentSet.sber())
                )
                .intradayValues(
                    generator().generateDeals(
                        DealsGeneratorConfig
                            .builder()
                            .ticker("SBER")
                            .numTrades(50000)
                            .startPrice(10.)
                            .startValue(100D)
                            .date(time.toLocalDate())
                            .startTime(LocalTime.parse("10:00"))
                            .pricePercentageGrowths(List.of(new PercentageGrowths(29D, 1D)))
                            .valuePercentageGrowths(List.of(new PercentageGrowths(29D, 1D)))
                            .build()
                    )
                )
                .historyValues(
                    generator().generateHistory(
                        HistoryGeneratorConfig
                            .builder()
                            .ticker("SBER")
                            .startClose(10.)
                            .startOpen(10.)
                            .startValue(1000D)
                            .days(180)
                            .startDate(startDate)
                            .openPricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                            .closePricePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                            .valuePercentageGrowths(List.of(new PercentageGrowths(5D, 1D)))
                            .build()
                    )
                )
                .build()
        );

        long startMills = System.currentTimeMillis();
        fullIntegrate();
        long finishMills = System.currentTimeMillis();
        long seconds = ((finishMills - startMills) / 1000);

        InstrumentResponse sber = getInstrumentById(getInstruments().get(0).getId());
        assertTrue(sber.getHistoryValues().size() >= 128);
        assertEquals(50000, sber.getIntradayValues().size());
        assertTrue(seconds < 60);
        System.out.println("seconds = " + seconds);
    }
}
