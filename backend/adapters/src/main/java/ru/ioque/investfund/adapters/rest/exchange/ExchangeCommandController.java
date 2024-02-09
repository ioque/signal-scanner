package ru.ioque.investfund.adapters.rest.exchange;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.request.DisableUpdateInstrumentRequest;
import ru.ioque.investfund.adapters.rest.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExchangeCommandController {
    ExchangeManager exchangeManager;

    @PostMapping("/api/v1/instruments/integrate")
    public void integrateInstruments() {
        exchangeManager.integrateWithDataSource();
    }

    @PostMapping("/api/v1/instruments/daily-integrate")
    public void integrateTradingData() {
        exchangeManager.integrateTradingData();
    }

    @PatchMapping("/api/v1/instruments/enable-update")
    public void enableUpdate(@RequestBody EnableUpdateInstrumentRequest request) {
        exchangeManager.enableUpdate(request.getInstrumentIds());
    }

    @PatchMapping("/api/v1/instruments/disable-update")
    public void disableUpdate(@RequestBody DisableUpdateInstrumentRequest request) {
        exchangeManager.disableUpdate(request.getInstrumentIds());
    }
}
