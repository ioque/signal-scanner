package ru.ioque.investfund.scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ioque.investfund.BaseTest;
import ru.ioque.investfund.application.modules.scanner.UpdateScannerCommand;
import ru.ioque.investfund.application.share.exception.ApplicationException;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.scanner.entity.anomalyvolume.AnomalyVolumeSignalConfig;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SIGNAL SCANNER MANAGER - CREATE SIGNAL SCANNER")
public class ScannerManagerTest extends BaseTest {
    @BeforeEach
    void initRepo() {
        exchangeManager().integrateWithDataSource();
    }

    @Test
    @DisplayName("""
        T1. Регистрация нового сканера сигналов для финансового инструмента с идентификатором AFKS.
        Инструмент интегрирован в систему. Дневные сделки и исторические данные не загружены.
        """)
    void testCase1() {
        addScanner(
            new AnomalyVolumeSignalConfig(
                1,
                "Аномальные объемы, третий эшелон.",
                getInstrumentIds(),
                1.5,
                180,
                "IMOEX")
        );
        assertFalse(
            signalProducerRepo()
                .getAll()
                .isEmpty()
        );
        assertEquals(
            "Аномальные объемы",
            signalProducerRepo()
                .getAll()
                .stream()
                .findFirst()
                .orElseThrow()
                .getAlgorithm()
                .getName()
        );
    }

    @Test
    @DisplayName("""
        T2. Регистрация нового сканера сигналов без финансовых инструментов.
        """)
    void testCase2() {
        var error = assertThrows(
            DomainException.class,
            () -> addScanner(
                new AnomalyVolumeSignalConfig(
                    1,
                    "Аномальные объемы, третий эшелон.",
                    null,
                    1.5,
                    180,
                    "IMOEX")
            )
        );
        assertTrue(error.getMessage().contains("Не передан список анализируемых инструментов."));
        assertEquals(0, signalProducerRepo().getAll().size());
    }

    @Test
    @DisplayName("""
        T3. Регистрация нового сканера сигналов с пустым списком финансовых инструментов.
        """)
    void testCase3() {
        var error = assertThrows(
            DomainException.class,
            () -> addScanner(
                new AnomalyVolumeSignalConfig(
                    1,
                    "Аномальные объемы, третий эшелон.",
                    List.of(),
                    1.5,
                    180,
                    "IMOEX")
            )
        );
        assertTrue(error.getMessage().contains("Не передан список анализируемых инструментов."));
        assertEquals(0, signalProducerRepo().getAll().size());
    }

    @Test
    @DisplayName("""
        T4. Регистрация нового сканера сигналов со списком из нескольких финансовых инструментов.
        """)
    void testCase4() {
        addScanner(
            new AnomalyVolumeSignalConfig(
                1,
                "Аномальные объемы, третий эшелон.",
                getInstrumentIds(),
                1.5,
                180,
                "IMOEX")
        );
        assertEquals(
            "Аномальные объемы",
            signalProducerRepo()
                .getAll()
                .stream()
                .findFirst()
                .orElseThrow()
                .getAlgorithm().getName()
        );
    }

    @Test
    @DisplayName("""
        T6. Обновление ранеее зарегистрированного сканера с изменением списка отслеживаемых инструментов.
        Результат: сканер сигналов обновлен.
        """)
    void testCase6() {
        addScanner(
            new AnomalyVolumeSignalConfig(
                1,
                "Старое описание",
                getInstruments()
                    .stream()
                    .filter(row -> row.getClass().equals(Stock.class))
                    .map(Instrument::getId)
                    .toList(),
                1.5,
                180,
                "IMOEX"
            )
        );
        int qnt = fakeDataScannerStorage().getAll().get(0).getObjectIds().size();

        dataScannerManager()
            .updateConfiguration(
                new UpdateScannerCommand(
                    fakeDataScannerStorage().getAll().get(0).getId(),
                    new AnomalyVolumeSignalConfig(
                        1,
                        "Старое описание",
                        getInstrumentIds(),
                        1.5,
                        180,
                        "IMOEX"
                    )
                )
            );

        assertNotEquals(qnt, fakeDataScannerStorage().getAll().get(0).getObjectIds().size());
        assertEquals(getInstruments().size(), fakeDataScannerStorage().getAll().get(0).getObjectIds().size());
    }

    @Test
    @DisplayName("""
        T7. Обновление ранеее зарегистрированного сканера с изменением описания.
        Результат: сканер сигналов обновлен.
        """)
    void testCase7() {
        addScanner(
            new AnomalyVolumeSignalConfig(
                1,
                "Старое описание",
                getInstrumentIds(),
                1.5,
                180,
                "IMOEX"
            )
        );

        dataScannerManager()
            .updateConfiguration(
                new UpdateScannerCommand(
                    fakeDataScannerStorage().getAll().get(0).getId(),
                    new AnomalyVolumeSignalConfig(
                        1,
                        "Новое описание",
                        getInstrumentIds(),
                        1.5,
                        180,
                        "IMOEX"
                    )
                )
            );

        assertEquals("Новое описание", fakeDataScannerStorage().getAll().get(0).getDescription());
        assertEquals(getInstruments().size(), fakeDataScannerStorage().getAll().get(0).getObjectIds().size());
    }

    @Test
    @DisplayName("""
        T8. Обновление несуществующего сканера.
        Результат: ошибка, сканер сигналов не найден.
        """)
    void testCase8() {
        UUID id = UUID.randomUUID();
        var error = assertThrows(
            ApplicationException.class,
            () -> dataScannerManager()
                .updateConfiguration(
                    new UpdateScannerCommand(
                        id,
                        new AnomalyVolumeSignalConfig(
                            1,
                            "Новое описание",
                            getInstrumentIds(),
                            1.5,
                            180,
                            "IMOEX"
                        )
                    )
                )
        );
        assertEquals("Сканер сигналов с идентификатором " + id + " не найден.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T9. Обновление ранеее зарегистрированного сканера, пустое описание.
        Результат: ошибка, "Описание сканера не может быть пустым."
        """)
    void testCase9() {
        addScanner(
            new AnomalyVolumeSignalConfig(
                1,
                "Старое описание",
                getInstrumentIds(),
                1.5,
                180,
                "IMOEX"
            )
        );
        var error = assertThrows(
            DomainException.class,
            () -> dataScannerManager()
                .updateConfiguration(
                    new UpdateScannerCommand(
                        fakeDataScannerStorage().getAll().get(0).getId(),
                        new AnomalyVolumeSignalConfig(
                            1,
                            "",
                            getInstrumentIds(),
                            1.5,
                            180,
                            "IMOEX"
                        )
                    )
                )
        );
        assertEquals("Не передано описание.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T10. Обновление ранеее зарегистрированного сканера, пустой список инструментов.
        Результат: ошибка, "Список анализируемых инструментов не может быть пуст."
        """)
    void testCase10() {
        addScanner(
            new AnomalyVolumeSignalConfig(
                1,
                "Старое описание",
                getInstrumentIds(),
                1.5,
                180,
                "IMOEX"
            )
        );
        var error = assertThrows(
            DomainException.class,
            () -> dataScannerManager()
                .updateConfiguration(
                    new UpdateScannerCommand(
                        fakeDataScannerStorage().getAll().get(0).getId(),
                        new AnomalyVolumeSignalConfig(
                            1,
                            "Старое описание",
                            List.of(),
                            1.5,
                            180,
                            "IMOEX"
                        )
                    )
                )
        );
        assertEquals("Не передан список анализируемых инструментов.", error.getMessage());
    }
}
