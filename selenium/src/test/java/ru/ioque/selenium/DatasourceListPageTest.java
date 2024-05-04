package ru.ioque.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatasourceListPageTest extends BaseFrontendTest {
    @Test
    @DisplayName("""
        Тестирование взаимодействия с элементами страницы "Список источников данных".
        """)
    public void verifyPage() {
        loadPageDatasourceList();
        final DatasourceCardContent cardContent = new DatasourceCardContent(driver.findElement(By.className("MuiCardContent-root")));
        assertEquals("Конфигурируемый источник данных", cardContent.title);
        assertEquals("Адрес шлюза: http://" + uiHost + ":8081", cardContent.gateway);
        assertEquals("Конфигурируемый источник данных, использовать для тестирования алгоритмов.", cardContent.description);
    }

    public class DatasourceCardContent {
        String title;
        String gateway;
        String description;

        public DatasourceCardContent(WebElement cardContent) {
            this.title = cardContent.findElement(By.className("MuiTypography-h5")).getText();
            this.gateway = cardContent.findElement(By.className("MuiTypography-body1")).getText();
            this.description = cardContent.findElement(By.className("MuiTypography-body2")).getText();
        }
    }
}
