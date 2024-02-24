package ru.ioque.investfund.adapters.rest.exchange;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name="ExchangeCommandController", description="Контроллер команд к модулю \"EXCHANGE\"")
public class ExchangeCommandController {
    ExchangeManager exchangeManager;
    IntradayValueEntityRepository intradayValueEntityRepository;

    @PostMapping("/api/v1/integrate")
    public void integrateInstruments() {
        exchangeManager.integrateWithDataSource();
    }

    @PostMapping("/api/v1/daily-integrate")
    public void integrateTradingData() {
        exchangeManager.integrateTradingData();
    }

    @PatchMapping("/api/v1/enable-update")
    public void enableUpdate(@RequestBody EnableUpdateInstrumentRequest request) {
        exchangeManager.enableUpdate(request.getInstrumentIds());
    }

    @PatchMapping("/api/v1/disable-update")
    public void disableUpdate(@RequestBody DisableUpdateInstrumentRequest request) {
        exchangeManager.disableUpdate(request.getInstrumentIds());
    }

    @DeleteMapping("/api/v1/intraday-value")
    public void clearIntradayValue() {
        intradayValueEntityRepository.deleteAll();
    }
}
