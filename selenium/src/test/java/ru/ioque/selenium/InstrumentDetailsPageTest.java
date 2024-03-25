package ru.ioque.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrumentDetailsPageTest extends BaseFrontendTest {
    @Test
    public void verifyPage() {
        loadPageInstrumentList();
        var ids = driver
            .findElement(By.className("table"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"))
            .stream().map(row -> row.findElements((By.xpath("./child::*"))).get(0).getText())
            .toList();
        verifyDetailsPage(ids.get(0), "BR-1.24 (BRF4)", 128, 1000);
        verifyDetailsPage(ids.get(1), "USDRUB_TOM (USD000UTSTOM)", 0, 0);
        verifyDetailsPage(ids.get(2), "Газпромнефть (SIBN)", 125, 999);
        verifyDetailsPage(ids.get(3), "Индекс фондового рынка мосбиржи (IMOEX)", 129, 1000);
    }

    private void verifyDetailsPage(String id, String name, int dailyValueQnt, int intradayValueQnt) {
        loadPageDetails(id);
        assertEquals(name, getHeaderElement().getText());
        assertEquals(dailyValueQnt, getDailyValueRows().size());
        assertEquals(intradayValueQnt, getIntradayValueRows().size());
    }

    private WebElement getHeaderElement() {
        return driver.findElement(By.className("accordion-button"));
    }

    private List<WebElement> getDailyValueRows() {
        return driver
            .findElement(By.id("dailyValueTable"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"));
    }

    private List<WebElement> getIntradayValueRows() {
        return driver
            .findElement(By.id("intradayValueTable"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"));
    }

    private void loadPageDetails(String id) {
        driver.get("http://" + uiHost + ":3000/instruments/" + id);
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("accordion-button")));
    }
}
