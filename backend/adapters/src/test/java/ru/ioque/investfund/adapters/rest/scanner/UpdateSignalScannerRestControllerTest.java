package ru.ioque.investfund.adapters.rest.scanner;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.BaseControllerTest;
import ru.ioque.investfund.adapters.rest.signalscanner.request.UpdateSignalScannerRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SIGNAL SCANNER REST CONTROLLER, ENDPOINT PATCH /signal-scanner/{id}")
public class UpdateSignalScannerRestControllerTest extends BaseControllerTest {
    private static final UUID SIGNAL_PRODUCER_ID = UUID.randomUUID();
    private static final UUID IMOEX_ID = UUID.randomUUID();
    private static final UUID TGKN_ID = UUID.randomUUID();

    @Test
    @SneakyThrows
    @DisplayName("""
        T5. Выполнение запроса по эндпоинту PATCH /api/v1/signal-scanner/{id}.
        """)
    public void testCase5() {
        assertIsOk(
            UpdateSignalScannerRequest
                .builder()
                .description("desc")
                .ids(List.of(TGKN_ID, IMOEX_ID))
                .build()
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T6. Выполнение запроса по эндпоинту PATCH /api/v1/signal-scanner/{id}. Не передано олисание.
        """)
    public void testCase6() {
        assertValidationErrors(
            UpdateSignalScannerRequest.builder()
                .ids(List.of(TGKN_ID, IMOEX_ID))
                .build(),
            List.of("The description is required.")
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T7. Выполнение запроса по эндпоинту PATCH /api/v1/signal-scanner/{id}. Передано пустое олисание.
        """)
    public void testCase7() {
        assertValidationErrors(
            UpdateSignalScannerRequest.builder()
                .description("")
                .ids(List.of(TGKN_ID, IMOEX_ID))
                .build(),
            List.of("The description is required.")
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T8. Выполнение запроса по эндпоинту PATCH /api/v1/signal-scanner/{id}. Не передан список объектов.
        """)
    public void testCase8() {
        assertValidationErrors(
            UpdateSignalScannerRequest.builder()
                .description("desc")
                .build(),
            List.of("The ids is required.")
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T9. Выполнение запроса по эндпоинту PATCH /api/v1/signal-scanner/{id}. Передан пустой список объектов.
        """)
    public void testCase9() {
        assertIsOk(
            UpdateSignalScannerRequest
                .builder()
                .description("desc")
                .ids(List.of())
                .build()
        );
    }

    @SneakyThrows
    private void assertValidationErrors(UpdateSignalScannerRequest request, List<String> errors) {
        mvc
            .perform(MockMvcRequestBuilders
                .patch("/api/v1/signal-scanner/" + SIGNAL_PRODUCER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(Map.of("errors", errors))
                    )
            );
    }

    @SneakyThrows
    private void assertIsOk(UpdateSignalScannerRequest request) {
        mvc
            .perform(MockMvcRequestBuilders
                .patch("/api/v1/signal-scanner/" + SIGNAL_PRODUCER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk());
    }
}
