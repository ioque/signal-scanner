package ru.ioque.investfund.application.modules.statistic;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.EventBus;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.application.modules.SystemModule;
import ru.ioque.investfund.domain.statistic.StatisticCalculatedEvent;
import ru.ioque.investfund.domain.statistic.StatisticCalculator;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatisticManager implements SystemModule {
    StatisticRepository statisticRepository;
    ExchangeRepository exchangeRepository;
    EventBus eventBus;
    UUIDProvider uuidProvider;
    DateTimeProvider dateTimeProvider;
    StatisticCalculator calculator = new StatisticCalculator();

    public StatisticManager(StatisticRepository statisticRepository,
                            ExchangeRepository exchangeRepository,
                            EventBus eventBus,
                            UUIDProvider uuidProvider,
                            DateTimeProvider dateTimeProvider
    ) {
        this.statisticRepository = statisticRepository;
        this.exchangeRepository = exchangeRepository;
        this.eventBus = eventBus;
        this.uuidProvider = uuidProvider;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public void execute() {
        calcStatistic();
    }

    public void calcStatistic() {
        statisticRepository.saveAll(
            exchangeRepository
                .getBy(dateTimeProvider.nowDate())
                .orElseThrow()
                .getInstruments()
                .stream()
                .filter(row -> !row.getIntradayValues().isEmpty() && !row.getDailyValues().isEmpty())
                .map(calculator::calcStatistic)
                .toList()
        );
        this.eventBus.publish(new StatisticCalculatedEvent(uuidProvider.generate(), dateTimeProvider.nowDateTime()));
    }
}
