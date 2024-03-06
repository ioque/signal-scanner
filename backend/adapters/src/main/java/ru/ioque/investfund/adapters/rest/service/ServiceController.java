package ru.ioque.investfund.adapters.rest.service;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.other.InMemoryEventBus;
import ru.ioque.investfund.adapters.rest.service.request.InitDateTimeRequest;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedDailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedIntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ScannerLogEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;

@Profile("test")
@RestController
@AllArgsConstructor
@Hidden
@Tag(name = "Служебный контролер", description = "Работает в окружении test, позволяет очищать стейт приложения.")
public class ServiceController {
    ExchangeEntityRepository exchangeEntityRepository;
    InstrumentEntityRepository instrumentEntityRepository;
    DailyValueEntityRepository dailyValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;
    ScannerLogEntityRepository scannerLogEntityRepository;
    SignalScannerEntityRepository signalScannerEntityRepository;
    ArchivedIntradayValueEntityRepository archivedIntradayValueEntityRepository;
    ArchivedDailyValueEntityRepository archivedDailyValueEntityRepository;
    InMemoryEventBus eventBus;
    ExchangeManager exchangeManager;
    DateTimeProvider dateTimeProvider;

    @PostMapping("/api/v1/service/date-time")
    public void initDateTime(@RequestBody InitDateTimeRequest request) {
        dateTimeProvider.initToday(request.getDate().atTime(request.getTime()));
    }

    @DeleteMapping("/api/v1/service/state")
    public void clearState() {
        instrumentEntityRepository.deleteAll();
        dailyValueEntityRepository.deleteAll();
        intradayValueEntityRepository.deleteAll();
        exchangeEntityRepository.deleteAll();
        scannerLogEntityRepository.deleteAll();
        signalScannerEntityRepository.deleteAll();
        archivedIntradayValueEntityRepository.deleteAll();
        archivedDailyValueEntityRepository.deleteAll();
        eventBus.clear();
        exchangeManager.clearCache();
        dateTimeProvider.initToday(null);
    }
}
