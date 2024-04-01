package ru.ioque.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BaseFrontendTest {
    protected String uiHost = System.getenv("UI_HOST");
    protected String uiPort = System.getenv("UI_PORT");

    protected WebDriver driver;

    protected String getUiUrl() {
        return uiHost + ":" + uiPort;
    }

    @BeforeEach
    public void beforeEach() {
        driver = new FirefoxDriver();
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void loadPageInstrumentList() {
        driver.get("http://" + getUiUrl() + "/instruments");
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
    }

    protected void loadPageScannerList() {
        driver.get("http://" + getUiUrl() + "/scanners");
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
    }
}
