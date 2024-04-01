package ru.ioque.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerListPageTest extends BaseFrontendTest {
    @Test
    @DisplayName("""
        Тестирование взаимодействия с основными элементами страницы.
        """)
    public void verifyPage() {
        loadPageScannerList();
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
        var tableRows = driver
            .findElement(By.className("table"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"));
        assertEquals(4, tableRows.size());
        assertTableRow(tableRows.get(0), "1", "Сканер сигналов с алгоритмом \"Аномальные объемы\": TGKN, TGKB, индекс IMOEX.", "1", "2024-04-01T10:00:00");
        assertTableRow(tableRows.get(1), "1", "Сканер сигналов с алгоритмом \"Дельта-анализ пар преф-обычка\": SBERP-SBER.", "1", "2024-04-01T10:00:00");
        assertTableRow(tableRows.get(2), "1440", "Сканер сигналов с алгоритмом \"Корреляция сектора с фьючерсом на базовый товар сектора\": TATN, ROSN, SIBN, LKOH, фьючерс BRF4.", "0", "2024-04-01T10:00:00");
        assertTableRow(tableRows.get(3), "60", "Сканер сигналов с алгоритмом \"Секторальный отстающий\": TATN, ROSN, SIBN, LKOH.", "0", "2024-04-01T10:00:00");

    }

    private void assertTableRow(WebElement webElement, String workPeriod, String description, String signalQnt, String lastExecution) {
        var columns = webElement.findElements((By.xpath("./child::*")));
        assertEquals(workPeriod, columns.get(2).getText());
        assertEquals(description, columns.get(3).getText());
        assertEquals(signalQnt, columns.get(4).getText());
        assertEquals(lastExecution, columns.get(5).getText());
    }
}
