package ru.ioque.apitest.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class HealthcheckAcceptanceTest {
    @Value("${variables.api_url}")
    String url;
    @Autowired
    RestTemplate restTemplate;

    @Test
    @DisplayName("Тестирование healthcheck backend-сервиса")
    void actuatorTest() {
        restTemplate.getForEntity(url + "/actuator/health", String.class);
    }

}
