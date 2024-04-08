package ru.ioque.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrumentDetailsPageTest extends BaseFrontendTest {
    @Test
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
        verifyDetailsPage(datasourceId, "BRF4", "BR-1.24 (BRF4)");
        verifyDetailsPage(datasourceId, "USD000UTSTOM", "USDRUB_TOM (USD000UTSTOM)");
        verifyDetailsPage(datasourceId, "SIBN", "Газпромнефть (SIBN)");
        verifyDetailsPage(datasourceId, "IMOEX", "Индекс фондового рынка мосбиржи (IMOEX)");
    }

    private void verifyDetailsPage(String datasourceId, String instrumentId, String name) {
        loadPageDetails(datasourceId, instrumentId);
        assertEquals(name, getHeaderElement().getText());
    }

    private WebElement getHeaderElement() {
        return driver.findElement(By.className("accordion-button"));
    }

    private void loadPageDetails(String datasourceId, String ticker) {
        driver.get("http://" + getUiUrl() + "/datasource/" + datasourceId + "/instrument/" + ticker);
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("accordion-button")));
    }
}
