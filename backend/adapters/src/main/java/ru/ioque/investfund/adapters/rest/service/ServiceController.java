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
import ru.ioque.investfund.adapters.storage.jpa.ExchangeCache;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedHistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ArchivedIntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.HistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ScannerLogEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.modules.datasource.DatasourceManager;

@Hidden
@RestController
@Profile("test")
@AllArgsConstructor
@Tag(name = "Служебный контролер", description = "Работает в окружении test, позволяет очищать стейт приложения.")
public class ServiceController {
    ExchangeEntityRepository exchangeEntityRepository;
    InstrumentEntityRepository instrumentEntityRepository;
    HistoryValueEntityRepository historyValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;
    ScannerLogEntityRepository scannerLogEntityRepository;
    SignalScannerEntityRepository signalScannerEntityRepository;
    ArchivedIntradayValueEntityRepository archivedIntradayValueEntityRepository;
    ArchivedHistoryValueEntityRepository archivedHistoryValueEntityRepository;
    ExchangeCache exchangeCache;
    InMemoryEventBus eventBus;
    DatasourceManager datasourceManager;
    DateTimeProvider dateTimeProvider;

    @PostMapping("/api/service/date-time")
    public void initDateTime(@RequestBody InitDateTimeRequest request) {
        dateTimeProvider.initToday(request.getDate().atTime(request.getTime()));
    }

    @DeleteMapping("/api/service/state")
    public void clearState() {
        instrumentEntityRepository.deleteAll();
        historyValueEntityRepository.deleteAll();
        intradayValueEntityRepository.deleteAll();
        exchangeEntityRepository.deleteAll();
        scannerLogEntityRepository.deleteAll();
        signalScannerEntityRepository.deleteAll();
        archivedIntradayValueEntityRepository.deleteAll();
        archivedHistoryValueEntityRepository.deleteAll();
        exchangeCache.clear();
        dateTimeProvider.initToday(null);
    }
}
