package ru.ioque.investfund.application.modules.statistic;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.domain.statistic.StatisticCalculator;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatisticManager {
    StatisticRepository statisticRepository;
    ExchangeRepository exchangeRepository;
    StatisticCalculator calculator = new StatisticCalculator();

    public void calcStatistic() {
        statisticRepository.saveAll(
            exchangeRepository
                .get()
                .getInstruments()
                .stream()
                .filter(row -> !row.getIntradayValues().isEmpty() && !row.getDailyValues().isEmpty())
                .map(calculator::calcStatistic)
                .toList()
        );
    }
}
