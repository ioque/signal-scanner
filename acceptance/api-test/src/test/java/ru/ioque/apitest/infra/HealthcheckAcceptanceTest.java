package ru.ioque.apitest.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import ru.ioque.apitest.DatasourceEmulatedTest;

public class HealthcheckAcceptanceTest extends DatasourceEmulatedTest {
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
