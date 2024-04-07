package ru.ioque.investfund.adapters.storage.jpa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.datasource.entity.Exchange;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EXCHANGE REPOSITORY TEST")
public class JpaDatasourceRepositoryTest extends BaseJpaTest {
    DatasourceRepository datasourceRepository;

    public JpaDatasourceRepositoryTest(
        @Autowired DatasourceRepository datasourceRepository
    ) {
        this.datasourceRepository = datasourceRepository;
    }

    @Test
    @DisplayName("Т1. Сохранение данных о бирже без инструментов")
    void testCase1() {
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of()
        );
        datasourceRepository.save(exchange);

        assertTrue(datasourceRepository.getBy(LocalDate.now()).isPresent());
        assertEquals(exchange, datasourceRepository.getBy(LocalDate.now()).get());
    }

    @Test
    @DisplayName("Т2. Сохранение данных о бирже с инструментами")
    void testCase2() {
        var instrument = buildAfks().build();
        Exchange exchange = new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                instrument
            )
        );
        datasourceRepository.save(exchange);

        assertTrue(datasourceRepository.getBy(LocalDate.now()).isPresent());
        assertEquals(1, datasourceRepository.getBy(LocalDate.now()).get().getInstruments().size());
        assertEquals(instrument, datasourceRepository.getBy(LocalDate.now()).get().getInstruments().get(0));
    }

    @Test
    @DisplayName("Т3. Обновление торговых данных.")
    void testCase3() {
        datasourceRepository.save(new Exchange(
            UUID.randomUUID(),
            "test",
            "test",
            "test",
            List.of(
                buildAfks()
                    .historyValues(List.of(buildTradingResult("AFKS", LocalDate.now().minusDays(1))))
                    .intradayValues(List.of(buildDealWith("AFKS", LocalDate.now().atTime(LocalTime.parse("10:00:00")))))
                    .build()
            )
        ));

        assertTrue(datasourceRepository.getBy(LocalDate.now()).isPresent());
        assertEquals(1, datasourceRepository.getBy(LocalDate.now()).get().getInstruments().size());
        assertEquals("AFKS", datasourceRepository.getBy(LocalDate.now()).get().getInstruments().get(0).getTicker());
        assertEquals(1, datasourceRepository.getBy(LocalDate.now()).get().getInstruments().get(0).getHistoryValues().size());
        assertEquals(1, datasourceRepository.getBy(LocalDate.now()).get().getInstruments().get(0).getIntradayValues().size());
        assertEquals("AFKS", datasourceRepository
            .getBy(LocalDate.now()).get().getInstruments().get(0).getHistoryValues().first().getTicker());
        assertEquals("AFKS", datasourceRepository
            .getBy(LocalDate.now()).get().getInstruments().get(0).getIntradayValues().first().getTicker());
    }
}
