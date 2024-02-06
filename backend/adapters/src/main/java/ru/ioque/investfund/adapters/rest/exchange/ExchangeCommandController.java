package ru.ioque.investfund.adapters.rest.exchange;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.rest.exchange.request.EnableUpdateRequest;
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

    @PostMapping("/api/v1/instruments/enable-update")
    public void enableUpdate(@RequestBody EnableUpdateRequest request) {
        exchangeManager.enableUpdate(request.getInstrumentIds());
    }
}
