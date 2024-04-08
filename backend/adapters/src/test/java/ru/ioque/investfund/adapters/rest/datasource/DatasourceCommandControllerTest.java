package ru.ioque.investfund.adapters.rest.datasource;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;
import ru.ioque.investfund.adapters.rest.datasource.request.DisableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.datasource.request.EnableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.datasource.request.RegisterDatasourceRequest;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("DATASOURCE COMMAND CONTROLLER TEST")
public class DatasourceCommandControllerTest extends BaseControllerTest {
    private static final UUID DATASOURCE_ID = UUID.randomUUID();

    @Test
    @SneakyThrows
    @DisplayName("""
         T1. Выполнение запроса по эндпоинту POST /api/datasource.
         """)
    public void testCase8() {
        mvc
            .perform(MockMvcRequestBuilders
                .post("/api/datasource")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    RegisterDatasourceRequest.builder()
                        .name("Московская биржа")
                        .url("http://localhost:8080")
                        .description("Московская биржа")
                        .build()
                ))
            )
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T2. Выполнение запроса по эндпоинту POST /api/datasource/{datasourceId}/instruments.
        """)
    public void testCase1() {
        mvc
            .perform(MockMvcRequestBuilders.post("/api/datasource/" + DATASOURCE_ID + "/instruments"))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T3. Выполнение запроса по эндпоинту POST /api/datasource/{datasourceId}/trading-data.
        """)
    public void testCase2() {
        mvc
            .perform(MockMvcRequestBuilders.post("/api/datasource/" + DATASOURCE_ID + "/trading-data"))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T4. Выполнение запроса по эндпоинту PATCH /api/datasource/{datasourceId}/enable-update.
        """)
    public void testCase3() {
        List<String> tickers = List.of("AFKS", "SBER");
        mvc
            .perform(MockMvcRequestBuilders
                .patch("/api/datasource/" + DATASOURCE_ID + "/enable-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new EnableUpdateInstrumentRequest(tickers)))
            )
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T5. Выполнение запроса по эндпоинту PATCH /api/datasource/{datasourceId}/disable-update.
        """)
    public void testCase4() {
        List<String> tickers = List.of("AFKS", "SBER");
        mvc
            .perform(MockMvcRequestBuilders
                .patch("/api/datasource/" + DATASOURCE_ID + "/disable-update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DisableUpdateInstrumentRequest(tickers)))
            )
            .andExpect(status().isOk());
    }
}
