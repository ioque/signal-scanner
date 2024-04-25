package ru.ioque.investfund.adapters.integration.logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ioque.investfund.adapters.integration.InfrastructureTest;
import ru.ioque.investfund.adapters.logger.AsyncFileLoggerProvider;
import ru.ioque.investfund.domain.core.ErrorLog;
import ru.ioque.investfund.domain.core.InfoLog;
import ru.ioque.investfund.domain.core.WarningLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ASYNC FILE LOGGER PROVIDER TEST")
public class AsyncFileLoggerProviderTest extends InfrastructureTest {
    @Autowired
    protected AsyncFileLoggerProvider provider;

    @BeforeEach
    void beforeEach() {
        Arrays
            .stream(Objects.requireNonNull(new File("build/logs/").listFiles()))
            .map(File::getAbsolutePath)
            .forEach(path -> {
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))){
                    writer.write("");
                    writer.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
    }

    @Test
    @DisplayName("""
        T1. Логирование InfoLog
        """)
    void testCase1() {
        UUID track = UUID.randomUUID();
        provider.log(
            new InfoLog(
                LocalDateTime.of(
                    LocalDate.of(2024, 4, 16),
                    LocalTime.of(10, 0, 0)
                ),
                "Test Log 1",
                track
            )
        );
        provider.log(
            new InfoLog(
                LocalDateTime.of(
                    LocalDate.of(2024, 4, 16),
                    LocalTime.of(10, 0, 0)
                ),
                "Test Log 2",
                track
            )
        );

        assertTrue(waitLogCount(2));
        final List<JsonNode> logContent = getLogContent();
        assertNotNull(logContent.get(0).get("time"));
        assertEquals(String.format("track = %s | Test Log 1", track), logContent.get(0).get("message").asText());
        assertEquals("INFO", logContent.get(0).get("level").asText());
        assertNotNull(logContent.get(1).get("time"));
        assertEquals(String.format("track = %s | Test Log 2", track), logContent.get(1).get("message").asText());
        assertEquals("INFO", logContent.get(1).get("level").asText());
    }

    @Test
    @DisplayName("""
        T2. Логирование ErrorLog
        """)
    void testCase2() {
        UUID track = UUID.randomUUID();
        provider.log(
            new ErrorLog(
                LocalDateTime.of(
                    LocalDate.of(2024, 4, 16),
                    LocalTime.of(10, 0, 0)
                ),
                "Test Error Log 1",
                new RuntimeException("Test Exception"),
                track
            )
        );

        assertTrue(waitLogCount(1));
        final List<JsonNode> logContent = getLogContent();
        assertNotNull(logContent.get(0).get("time"));
        assertEquals("ERROR", logContent.get(0).get("level").asText());
        assertEquals(String.format("track = %s | Test Error Log 1", track), logContent.get(0).get("message").asText());
        assertEquals("java.lang.RuntimeException", logContent.get(0).get("error_type").asText());
        assertEquals("Test Exception", logContent.get(0).get("error_message").asText());
    }

    @Test
    @DisplayName("""
        T2. Логирование WarningLog
        """)
    void testCase3() {
        UUID track = UUID.randomUUID();
        provider.log(
            new WarningLog(
                LocalDateTime.of(
                    LocalDate.of(2024, 4, 16),
                    LocalTime.of(10, 0, 0)
                ),
                "Test Warning Log 1",
                track
            )
        );

        assertTrue(waitLogCount(1));
        var logContent = getLogContent();
        assertNotNull(logContent.get(0).get("time"));
        assertEquals("WARN", logContent.get(0).get("level").asText());
        assertEquals(String.format("track = %s | Test Warning Log 1", track), logContent.get(0).get("message").asText());
    }

    private boolean waitLogCount(int logCount) {
        long startTime = System.currentTimeMillis();
        while (getLogContent().size() != logCount) {
            if (System.currentTimeMillis() - startTime > 1000) {
                return false;
            }
        }
        return true;
    }

    @SneakyThrows
    private List<JsonNode> getLogContent() {
        List<String> lines = new ArrayList<>();
        Path path = Path.of("build/logs/trading-data-processor-business.log");

        try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }

        return lines.stream().map(line -> {
            try {
                return new ObjectMapper().readTree(line);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
