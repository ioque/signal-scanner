package ru.ioque.investfund.adapters.config;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.domain.datasource.event.TradingDataUpdatedEvent;

@Component
public class EventBusConfigurator {
    public EventBusConfigurator(ScannerManager scannerManager, EventBus eventBus) {
        eventBus.subscribe(TradingDataUpdatedEvent.class, scannerManager);
    }
}
