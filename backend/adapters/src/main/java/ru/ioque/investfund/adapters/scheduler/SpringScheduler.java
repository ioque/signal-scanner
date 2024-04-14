package ru.ioque.investfund.adapters.scheduler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SpringScheduler {
//    @Async
//    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
//    public void integrateTradingData() {
//        exchangeManager.execute();
//    }
}
