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
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, 180, "IMOEX")
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
                .getConfig()
                .factorySearchAlgorithm()
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
                "Аномальные объемы, третий эшелон.",
                new AnomalyVolumeSignalConfig(null, 1.5, 180, "IMOEX")
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
                "Аномальные объемы, третий эшелон.",
                new AnomalyVolumeSignalConfig(List.of(), 1.5, 180, "IMOEX")
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
            "Аномальные объемы, третий эшелон.",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, 180, "IMOEX")
        );
        assertEquals(
            "Аномальные объемы",
            signalProducerRepo()
                .getAll()
                .stream()
                .findFirst()
                .orElseThrow()
                .getConfig()
                .factorySearchAlgorithm().getName()
        );
    }

    @Test
    @DisplayName("""
        T5. Регистрация нового сканера сигналов без конфигурации.
        """)
    void testCase5() {
        var error = assertThrows(
            DomainException.class,
            () -> addScanner("Аномальные объемы, третий эшелон.", null)
        );
        assertEquals("Не передана конфигурация алгоритма.", error.getMessage());
    }

    @Test
    @DisplayName("""
        T6. Обновление ранеее зарегистрированного сканера с изменением списка отслеживаемых инструментов.
        Результат: сканер сигналов обновлен.
        """)
    void testCase6() {
        addScanner(
            "Старое описание",
            new AnomalyVolumeSignalConfig(
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
            .updateScanner(
                new UpdateScannerCommand(
                    fakeDataScannerStorage().getAll().get(0).getId(),
                    "Старое описание",
                    getInstruments().stream().map(Instrument::getId).toList()
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
            "Старое описание",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, 180, "IMOEX")
        );

        dataScannerManager()
            .updateScanner(
                new UpdateScannerCommand(
                    fakeDataScannerStorage().getAll().get(0).getId(),
                    "Новое описание",
                    getInstruments().stream().map(Instrument::getId).toList()
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
                .updateScanner(
                    new UpdateScannerCommand(
                        id,
                        "Новое описание",
                        getInstruments().stream().map(Instrument::getId).toList()
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
            "Старое описание",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, 180, "IMOEX")
        );
        var error = assertThrows(
            DomainException.class,
            () -> dataScannerManager()
                .updateScanner(
                    new UpdateScannerCommand(
                        fakeDataScannerStorage().getAll().get(0).getId(),
                        "",
                        getInstruments().stream().map(Instrument::getId).toList()
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
            "Старое описание",
            new AnomalyVolumeSignalConfig(getInstrumentIds(), 1.5, 180, "IMOEX")
        );
        var error = assertThrows(
            DomainException.class,
            () -> dataScannerManager()
                .updateScanner(
                    new UpdateScannerCommand(
                        fakeDataScannerStorage().getAll().get(0).getId(),
                        "Старое описание",
                        List.of()
                    )
                )
        );
        assertEquals("Не передан список анализируемых инструментов.", error.getMessage());
    }
}
