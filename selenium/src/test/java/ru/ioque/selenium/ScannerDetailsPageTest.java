package ru.ioque.selenium;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScannerDetailsPageTest extends BaseFrontendTest {
    @Test
    public void verifyGitHubTitle() {
        driver.get("http://localhost:3000");
        assertTrue(driver.getTitle().contains("Система сигналов"));
    }
}
