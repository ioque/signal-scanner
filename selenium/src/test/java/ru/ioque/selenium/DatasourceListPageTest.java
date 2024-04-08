package ru.ioque.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatasourceListPageTest extends BaseFrontendTest {
    @Test
    @DisplayName("""
        Тестирование взаимодействия с основными элементами списка источников данных.
        """)
    public void verifyPage() {
        loadPageDatasourceList();
        var ids = driver
            .findElement(By.className("table"))
            .findElements(By.xpath("./child::*"))
            .get(1)
            .findElements(By.xpath("./child::*"))
            .stream().map(row -> row.findElements((By.xpath("./child::*"))).get(0).getText())
            .toList();
        assertEquals(1, ids.size());
    }
}
