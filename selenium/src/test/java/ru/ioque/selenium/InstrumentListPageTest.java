package ru.ioque.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrumentListPageTest extends BaseFrontendTest {
    @Test
    @DisplayName("""
        Тестирование взаимодействия с основными элементами страницы.
        """)
    public void verifyPage() {
        loadPageDatasourceList();
        final String datasourceId = driver
            .findElement(By.className("table"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"))
            .stream().map(row -> row.findElements((By.xpath("./child::*"))).get(0).getText())
            .toList()
            .get(0);
        loadPageInstrumentList(datasourceId);
        verifyExchangeElement();
        verifyInstrumentList();
    }

    private void verifyExchangeElement() {
        var exchangeHeaderElement = driver.findElement(By.xpath ("//*[contains(text(),'Конфигурируемый источник данных')]"));
        assertEquals("Конфигурируемый источник данных", exchangeHeaderElement.getText());
    }

    private void verifyInstrumentList() {
        var tableRows = driver
            .findElement(By.className("table"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"));
        assertEquals(11, tableRows.size());
        assertTableRow(tableRows.get(0), "BRF4", "BR-1.24");
        assertTableRow(tableRows.get(1), "USD000UTSTOM", "USDRUB_TOM");
        assertTableRow(tableRows.get(2), "SIBN", "Газпромнефть");
        assertTableRow(tableRows.get(3), "IMOEX", "Индекс фондового рынка мосбиржи");
        assertTableRow(tableRows.get(4), "LKOH", "Лукойл");
        assertTableRow(tableRows.get(5), "ROSN", "Роснефть");
        assertTableRow(tableRows.get(6), "SBER", "Сбербанк");
        assertTableRow(tableRows.get(7), "SBERP", "Сбербанк-п");
        assertTableRow(tableRows.get(8), "TATN", "Татнефть");
        assertTableRow(tableRows.get(9), "TGKB", "ТГК-11");
        assertTableRow(tableRows.get(10), "TGKN", "ТГК-14");
    }

    private void assertTableRow(WebElement webElement, String ticker, String shortName) {
        var columns = webElement.findElements((By.xpath("./child::*")));
        assertEquals(ticker, columns.get(0).getText());
        assertEquals(shortName, columns.get(1).getText());
    }
}
