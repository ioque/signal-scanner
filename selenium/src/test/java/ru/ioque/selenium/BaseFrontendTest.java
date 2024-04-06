package ru.ioque.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
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

    protected void loadPageInstrumentList() {
        driver.get("http://" + getUiUrl() + "/instruments");
        new WebDriverWait(driver, ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
    }

    protected void loadPageScannerList() {
        driver.get("http://" + getUiUrl() + "/scanners");
        new WebDriverWait(driver, ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("table")));
    }

    private ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--start-maximized"); // disabling extensions
        options.addArguments("--disable-gpu"); // applicable to windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // Bypass OS security model
        options.addArguments("--lang=en");
        options.addArguments("--remote-allow-origins=*");
        options.setPageLoadTimeout(DELAY);
        options.setImplicitWaitTimeout(DELAY);
        options.setScriptTimeout(DELAY);
        return options;
    }
}
