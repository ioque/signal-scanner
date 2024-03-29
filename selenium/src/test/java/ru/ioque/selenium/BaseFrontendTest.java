package ru.ioque.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;

public class BaseFrontendTest {
    protected String uiHost = System.getenv("FRONTEND_HOST");

    protected WebDriver driver;

    @BeforeEach
    public void beforeEach() {
        HashMap<String, Object> chromePrefs = new HashMap<>();
        ChromeOptions options = new ChromeOptions();
        String downloadFilepath = System.getProperty("downloadFilepath");
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920x1080");

        driver = new ChromeDriver(options);
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void loadPageInstrumentList() {
        driver.get("http://" + uiHost + ":4200/instruments");
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
    }

    protected void loadPageScannerList() {
        driver.get("http://" + uiHost + ":4200/scanners");
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
    }
}
