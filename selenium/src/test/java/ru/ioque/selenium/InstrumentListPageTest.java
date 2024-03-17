package ru.ioque.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrumentListPageTest extends BaseFrontendTest {
    @Test
    @DisplayName("""
        Тестирование взаимодействия с основными элементами страницы.
        """)
    public void verifyPage() {
        driver.get("http://localhost:3000/instruments");
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState")
                .equals("complete")
            );
        verifyExchangeElement();
        verifyInstrumentList();
    }

    private void verifyExchangeElement() {
        var exchangeHeaderElement = driver.findElement(By.xpath ("//*[contains(text(),'Московская Биржа')]"));
        exchangeHeaderElement.click();
        var exchangeBodyElements = driver.findElement(By.className("accordion-body")).findElements(By.xpath("./child::*"));
        assertEquals("Московская Биржа", exchangeHeaderElement.getText());
        assertEquals("Адрес шлюза", exchangeBodyElements.get(0).getText());
        assertEquals("http://localhost:8081", exchangeBodyElements.get(1).getText());
        assertEquals("Описание", exchangeBodyElements.get(2).getText());
        assertEquals("Московская биржа, интегрируются только данные основных торгов: TQBR, RFUD, SNDX, CETS.", exchangeBodyElements.get(3).getText());
    }

    private void verifyInstrumentList() {
        var tableRows = driver
            .findElement(By.className("table"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"));
        assertEquals(10, tableRows.size());
        assertTableRow(tableRows.get(0), "BRF4", "BR-1.24");
        assertTableRow(tableRows.get(1), "USD000UTSTOM", "USDRUB_TOM");
        assertTableRow(tableRows.get(2), "SIBN", "Газпромнефть");
        assertTableRow(tableRows.get(3), "IMOEX", "Индекс фондового рынка мосбиржи");
        assertTableRow(tableRows.get(4), "LKOH", "Лукойл");
        assertTableRow(tableRows.get(5), "ROSN", "Роснефть");
        assertTableRow(tableRows.get(6), "SBER", "Сбербанк");
        assertTableRow(tableRows.get(7), "SBERP", "Сбербанк-п");
        assertTableRow(tableRows.get(8), "TATN", "Татнефть");
        assertTableRow(tableRows.get(9), "TGKN", "ТГК-14");
    }

    private void assertTableRow(WebElement webElement, String ticker, String shortName) {
        var columns = webElement.findElements((By.xpath("./child::*")));
        assertEquals(ticker, columns.get(1).getText());
        assertEquals(shortName, columns.get(2).getText());
    }
}
