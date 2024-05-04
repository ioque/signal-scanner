package ru.ioque.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerListPageTest extends BaseFrontendTest {
    @Test
    @DisplayName("""
        Тестирование взаимодействия с элементами страницы "Список сканеров".
        """)
    public void verifyPage() {
        loadPageScannerList();

        final ScannerTableHeaderElement header = new ScannerTableHeaderElement(
            driver.findElement(By.className("MuiTableHead-root"))
        );
        final ScannerTableContent content = new ScannerTableContent(
            driver.findElement(By.className("MuiTableBody-root"))
        );
        assertEquals("Идентификатор", header.idColumn.getText());
        assertEquals("Описание", header.descriptionColumn.getText());
        assertEquals("Последний запуск", header.lastExecutionDateTimeColumn.getText());
        assertEquals(4, content.rows.size());
    }

    public class ScannerTableContentRow {
        String id;
        String description;
        String lastExecutionDateTime;
        String todayPrice;

        public ScannerTableContentRow(WebElement row) {
            List<WebElement> columns = row.findElements(By.className("MuiTableCell-root"));
            id = columns.get(0).getText();
            description = columns.get(1).getText();
            lastExecutionDateTime = columns.get(2).getText();
        }
    }

    public class ScannerTableContent {
        List<ScannerTableContentRow> rows = new ArrayList<>();

        public ScannerTableContent(WebElement tableBody) {
            rows.addAll(
                tableBody
                    .findElements(By.className("MuiTableRow-root"))
                    .stream()
                    .map(ScannerTableContentRow::new)
                    .toList()
            );
        }
    }

    public class ScannerTableHeaderElement {
        WebElement idColumn;
        WebElement descriptionColumn;
        WebElement lastExecutionDateTimeColumn;

        public ScannerTableHeaderElement(WebElement tableHeader) {
            List<WebElement> headerColumns = tableHeader
                .findElement(By.className("MuiTableRow-head"))
                .findElements(By.className("MuiTableCell-head"));
            idColumn = headerColumns.get(0);
            descriptionColumn = headerColumns.get(1);
            lastExecutionDateTimeColumn = headerColumns.get(2);
        }
    }
}
