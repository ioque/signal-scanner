package ru.ioque.investfund.adapters.rest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ioque.investfund.adapters.rest.signalscanner.request.PrefSimpleRequestAdd;
import ru.ioque.investfund.adapters.rest.signalscanner.response.SignalScannerResponse;
import ru.ioque.investfund.adapters.storage.jpa.JpaScannerRepo;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.scanner.financial.algorithms.AnomalyVolumeSignalConfig;
import ru.ioque.investfund.domain.scanner.financial.entity.SignalScannerBot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("SIGNAL SCANNER REST INTERFACE")
public class SignalScannerControllerTest extends BaseControllerTest {
    @Autowired
    ScannerManager scannerManager;
    @Autowired
    JpaScannerRepo dataJpaScannerRepo;
    private static final UUID SIGNAL_PRODUCER_ID = UUID.randomUUID();

    @Test
    @SneakyThrows
    @DisplayName("""
        T1. Выполнение запроса по эндпоинту GET /api/v1/signal-scanner.
        """)
    public void testCase1() {
        var signalProducers = getSignalProducers();

        Mockito
            .when(dataJpaScannerRepo.getAll())
            .thenReturn(signalProducers);

        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/signal-scanner"))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                signalProducers
                                    .stream()
                                    .map(SignalScannerResponse::fromDomain)
                                    .toList()
                            )
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T2. Выполнение запроса по эндпоинту GET /api/v1/signal-scanner/{id}.
        """)
    public void testCase2() {
        final var signalProducers = getSignalProducers();
        final var uuid = getSignalProducers().stream().findFirst().orElseThrow().getId();
        Mockito
            .when(dataJpaScannerRepo.getBy(uuid))
            .thenReturn(Optional.of(getSignalProducers().stream().findFirst().orElseThrow()));

        mvc
            .perform(MockMvcRequestBuilders.get("/api/v1/signal-scanner/" + uuid))
            .andExpect(status().isOk())
            .andExpect(
                content()
                    .json(
                        objectMapper
                            .writeValueAsString(
                                signalProducers
                                    .stream()
                                    .findFirst()
                                    .map(SignalScannerResponse::fromDomain)
                                    .orElseThrow()
                            )
                    )
            );
    }

    @Test
    @SneakyThrows
    @DisplayName("""
        T3. Выполнение запроса по эндпоинту POST /api/v1/signal-scannerr.
        """)
    public void testCase3() {
        mvc
            .perform(
                MockMvcRequestBuilders
                    .post("/api/v1/signal-scanner")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(PrefSimpleRequestAdd
                        .builder()
                        .description("Описание")
                        .ids(List.of(UUID.randomUUID()))
                        .spreadParam(1.0)
                        .build()))
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @SneakyThrows
    public String toJson(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    private List<SignalScannerBot> getSignalProducers() {
        List<Instrument> finInstruments = List.of(
            Stock.builder()
                .id(UUID.randomUUID())
                .ticker("AFKS")
                .name("ао Система")
                .build()
        );
        return List.of(
            new SignalScannerBot(
                SIGNAL_PRODUCER_ID,
                "Описание",
                finInstruments.stream().map(Instrument::getId).toList(),
                new AnomalyVolumeSignalConfig(1.5, 180, "IMOEX")
            )
        );
    }
}
