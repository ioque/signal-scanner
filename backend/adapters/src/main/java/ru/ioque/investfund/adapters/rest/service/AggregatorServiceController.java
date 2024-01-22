package ru.ioque.investfund.adapters.rest.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueRepository;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;

@Profile("test")
@RestController
@AllArgsConstructor
public class AggregatorServiceController {
    InstrumentEntityRepository instrumentEntityRepository;
    DailyValueRepository dailyValueRepository;
    IntradayValueRepository intradayValueRepository;
    ExchangeManager exchangeManager;

    @DeleteMapping("/api/v1/instruments")
    public void deleteInstruments() {
        instrumentEntityRepository.deleteAll();
        dailyValueRepository.deleteAll();
        intradayValueRepository.deleteAll();
        exchangeManager.clearCache();
    }
}
