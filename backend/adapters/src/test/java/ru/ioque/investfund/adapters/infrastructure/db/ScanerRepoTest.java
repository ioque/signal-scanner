package ru.ioque.investfund.adapters.infrastructure.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.persistence.PsqlScannerRepository;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;
import ru.ioque.investfund.domain.scanner.value.algorithms.properties.AnomalyVolumeProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SCANER REPOSITORY TEST")
public class ScanerRepoTest extends DatabaseTest {
    PsqlScannerRepository psqlScannerRepository;

    public ScanerRepoTest(
        @Autowired PsqlScannerRepository psqlScannerRepository
    ) {
        this.psqlScannerRepository = psqlScannerRepository;
    }

    @Test
    @DisplayName("""
        T1. Выгрузка сканера по его идентификатору
        """)
    void testCase1() {
        prepareState();
        psqlScannerRepository.save(
            createScanner(
                List.of("TGKN", "TGKB", "IMOEX"),
                createAnomalyVolumeProperties(1.5, 180, "IMOEX")
            )
        );

        final Optional<SignalScanner> scanner = psqlScannerRepository.getBy(SCANNER_ID);
        assertTrue(scanner.isPresent());
        assertTrue(List.of("TGKN", "TGKB", "IMOEX").containsAll(scanner.get().getTickers()));

    }

    private SignalScanner createScanner(List<String> tickers, AnomalyVolumeProperties properties) {
        return SignalScanner.builder()
            .id(SCANNER_ID)
            .datasourceId(exchangeEntityRepository.findAll().get(0).getId())
            .tickers(tickers)
            .properties(properties)
            .description("desc")
            .signals(new ArrayList<>())
            .workPeriodInMinutes(1)
            .lastExecutionDateTime(LocalDateTime.now())
            .build();
    }

    private AnomalyVolumeProperties createAnomalyVolumeProperties(double scaleCoefficient, int historyPeriod, String indexTicker) {
        return new AnomalyVolumeProperties(scaleCoefficient, historyPeriod, indexTicker);
    }
}
