package ru.ioque.investfund.adapters.rest.service;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ioque.investfund.adapters.storage.jpa.repositories.DailyValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ExchangeEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ReportEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.ScheduleUnitEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.SignalScannerEntityRepository;
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
    ReportEntityRepository reportEntityRepository;
    ScheduleUnitEntityRepository scheduleUnitEntityRepository;
    SignalScannerEntityRepository signalScannerEntityRepository;
    ExchangeManager exchangeManager;

    @DeleteMapping("/api/v1/service/state")
    public void clearState() {
        instrumentEntityRepository.deleteAll();
        dailyValueEntityRepository.deleteAll();
        intradayValueEntityRepository.deleteAll();
        exchangeEntityRepository.deleteAll();
        reportEntityRepository.deleteAll();
        signalScannerEntityRepository.deleteAll();
        scheduleUnitEntityRepository.deleteAll();
        exchangeManager.clearCache();
    }
}
