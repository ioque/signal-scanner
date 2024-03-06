package ru.ioque.investfund.application.modules.statistic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.application.modules.exchange.ExchangeCache;
import ru.ioque.investfund.domain.statistic.StatisticCalculator;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatisticManager {
    StatisticRepository statisticRepository;
    ExchangeCache exchangeCache;

    public void calcStatistic() {
        StatisticCalculator calculator = new StatisticCalculator();
        exchangeCache.get().ifPresent(exchange -> statisticRepository.saveAll(exchange.getInstruments().stream().map(calculator::calcStatistic).toList()));
    }
}
