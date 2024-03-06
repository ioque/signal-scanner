package ru.ioque.investfund.adapters.config;

import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.modules.scanner.ScannerManager;
import ru.ioque.investfund.application.modules.statistic.StatisticManager;
import ru.ioque.investfund.domain.exchange.entity.TradingDataUpdatedEvent;
import ru.ioque.investfund.domain.statistic.StatisticCalculatedEvent;

@Component
public class EventBusConfigurator {
    public EventBusConfigurator(ScannerManager scannerManager, StatisticManager statisticManager, EventBus eventBus) {
        eventBus.subscribe(TradingDataUpdatedEvent.class, statisticManager);
        eventBus.subscribe(StatisticCalculatedEvent.class, scannerManager);
    }
}
