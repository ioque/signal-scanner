package ru.ioque.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static java.time.Duration.ofSeconds;

public class BaseFrontendTest {
    public static final Duration DELAY = ofSeconds(5);
    protected String uiHost = System.getenv("UI_HOST");
    protected String uiPort = System.getenv("UI_PORT");
    protected String remoteWebDriverUrl = System.getenv("REMOTE_WEB_DRIVER_URL");

    protected WebDriver driver;

    protected String getUiUrl() {
        return uiHost + ":" + uiPort;
    }

    @BeforeEach
    void setupTest() {
        if (remoteWebDriverUrl != null) {
            driver = new RemoteWebDriver(
                getRemoteWebDriverUrl(),
                chromeOptions()
            );
        } else {
            driver = new ChromeDriver(chromeOptions());
        }
    }

    private URL getRemoteWebDriverUrl() {
        try {
            return new URL(remoteWebDriverUrl);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @AfterEach
    public void afterEach() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void loadPageDatasourceList() {
        driver.get("http://" + getUiUrl() + "/datasource");
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiCard-root")));
    }

    protected void loadPageScannerList() {
        driver.get("http://" + getUiUrl() + "/scanner");
        new WebDriverWait(driver, ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("MuiTable-root")));
    }

    private ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--lang=en");
        options.addArguments("--remote-allow-origins=*");
        options.setPageLoadTimeout(DELAY);
        options.setImplicitWaitTimeout(DELAY);
        options.setScriptTimeout(DELAY);
        return options;
    }
}
