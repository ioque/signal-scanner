package ru.ioque.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrumentListPageTest extends BaseFrontendTest {
    @Test
    @DisplayName("""
        Тестирование взаимодействия с основными элементами страницы "Cписок инструментов".
        """)
    public void verifyPage() {
        loadPageDatasourceList();
        WebElement linkToInstrument = driver.findElement(By.className("MuiButton-root"));
        linkToInstrument.click();
        new WebDriverWait(driver, ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiTable-root")));
        final InstrumentTableHeaderElement header = new InstrumentTableHeaderElement(
            driver.findElement(By.className("MuiTableHead-root"))
        );
        final InstrumentTableContent content = new InstrumentTableContent(
            driver.findElement(By.className("MuiTableBody-root"))
        );
        assertEquals("Тикер", header.tickerColumn.getText());
        assertEquals("Наименование", header.shortNameColumn.getText());
        assertEquals("Текущий объем", header.todayValueColumn.getText());
        assertEquals("Последняя цена", header.todayPriceColumn.getText());
        assertEquals(11, content.rows.size());
    }

    public class InstrumenTableContentRow {
        String ticker;
        String shortName;
        String todayValue;
        String todayPrice;

        public InstrumenTableContentRow(WebElement row) {
            List<WebElement> columns = row.findElements(By.className("MuiTableCell-root"));
            ticker = columns.get(0).getText();
            shortName = columns.get(1).getText();
            todayValue = columns.get(2).getText();
            todayPrice = columns.get(3).getText();
        }
    }

    public class InstrumentTableContent {
        List<InstrumenTableContentRow> rows = new ArrayList<>();

        public InstrumentTableContent(WebElement tableBody) {
            rows.addAll(
                tableBody
                    .findElements(By.className("MuiTableRow-root"))
                    .stream()
                    .map(InstrumenTableContentRow::new)
                    .toList()
            );
        }
    }

    public class InstrumentTableHeaderElement {
        WebElement tickerColumn;
        WebElement shortNameColumn;
        WebElement todayValueColumn;
        WebElement todayPriceColumn;

        public InstrumentTableHeaderElement(WebElement tableHeader) {
            List<WebElement> headerColumns = tableHeader
                .findElement(By.className("MuiTableRow-head"))
                .findElements(By.className("MuiTableCell-head"));
            tickerColumn = headerColumns.get(0);
            shortNameColumn = headerColumns.get(1);
            todayValueColumn = headerColumns.get(2);
            todayPriceColumn = headerColumns.get(3);
        }
    }
}
