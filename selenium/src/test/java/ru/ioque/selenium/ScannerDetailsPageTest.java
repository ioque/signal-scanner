package ru.ioque.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerDetailsPageTest extends BaseFrontendTest {
    @Test
    public void verifyPage() {
        loadPageScannerList();
        var ids = driver
            .findElement(By.className("table"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"))
            .stream().map(row -> row.findElements((By.xpath("./child::*"))).get(1).getText())
            .toList();
        verifyAnomalyVolumeScannerPage(ids.get(0));
        verifyPrefSimpleScannerPage(ids.get(1));
        verifyCorrelationSectoralFuturesScannerPage(ids.get(2));
        verifySectoralRetardScannerPage(ids.get(3));
    }

    private void verifyAnomalyVolumeScannerPage(String id) {
        loadPageDetails(id);
        openAccordion();
        assertEquals(
            "Сканер сигналов с алгоритмом \"Аномальные объемы\": TGKN, TGKB, индекс IMOEX.",
            getHeaderElement().getText()
        );
        assertEquals(3, getAccordionBodyElements().size());
        assertEquals("Величина scaleCoefficient: 1.5", getAccordionBodyElements().get(0).getText());
        assertEquals("Период исторических данных: 180", getAccordionBodyElements().get(1).getText());
        assertEquals("Тикер индекса: IMOEX", getAccordionBodyElements().get(2).getText());
        assertEquals(1, getSignalRows().size());
        assertEquals(3, getLogRows().size());
    }

    private void verifyPrefSimpleScannerPage(String id) {
        loadPageDetails(id);
        openAccordion();
        assertEquals(
            "Сканер сигналов с алгоритмом \"Дельта-анализ пар преф-обычка\": SBERP-SBER.",
            getHeaderElement().getText()
        );
        assertEquals(1, getAccordionBodyElements().size());
        assertEquals("Величина спреда: 1", getAccordionBodyElements().get(0).getText());
        assertEquals(1, getSignalRows().size());
        assertEquals(3, getLogRows().size());
    }

    private void verifyCorrelationSectoralFuturesScannerPage(String id) {
        loadPageDetails(id);
        openAccordion();
        assertEquals(
            "Сканер сигналов с алгоритмом \"Корреляция сектора с фьючерсом на базовый товар сектора\": TATN, ROSN, SIBN, LKOH, фьючерс BRF4.",
            getHeaderElement().getText()
        );
        assertEquals(3, getAccordionBodyElements().size());
        assertEquals("Величина futuresOvernightScale: 0.015", getAccordionBodyElements().get(0).getText());
        assertEquals("Величина stockOvernightScale: 0.015", getAccordionBodyElements().get(1).getText());
        assertEquals("Тикер фьючерса: BRF4", getAccordionBodyElements().get(2).getText());
        assertEquals(0, getSignalRows().size());
        assertEquals(6, getLogRows().size());
    }

    private WebElement getHeaderElement() {
        return driver.findElement(By.className("accordion-button"));
    }

    private void verifySectoralRetardScannerPage(String id) {
        loadPageDetails(id);
        openAccordion();
        assertEquals(
            "Сканер сигналов с алгоритмом \"Секторальный отстающий\": TATN, ROSN, SIBN, LKOH.",
            getHeaderElement().getText()
        );
        assertEquals(2, getAccordionBodyElements().size());
        assertEquals("Величина historyScale: 0.015", getAccordionBodyElements().get(0).getText());
        assertEquals("Величина intradayScale: 0.015", getAccordionBodyElements().get(1).getText());
        assertEquals(0, getSignalRows().size());
        assertEquals(3, getLogRows().size());
    }

    private void openAccordion() {
        getHeaderElement().click();
    }

    private List<WebElement> getLogRows() {
        return driver
            .findElement(By.id("logTable"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"));
    }

    private List<WebElement> getSignalRows() {
        return driver
            .findElement(By.id("signalTable"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"));
    }

    private List<WebElement> getAccordionBodyElements() {
        return driver
            .findElement(By.className("accordion-body"))
            .findElements(By.xpath("./child::*"))
            .get(0)
            .findElements(By.xpath("./child::*"));
    }

    private void loadPageDetails(String id) {
        driver.get("http://" + uiHost + ":3000/scanners/" + id);
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("accordion-button")));
    }
}
