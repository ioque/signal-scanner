package ru.ioque.investfund.adapters.other;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.modules.exchange.ExchangeManager;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SpringScheduler {
    ExchangeManager exchangeManager;
    @Async
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void integrateTradingData() {
        exchangeManager.execute();
    }
}
