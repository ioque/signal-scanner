package ru.ioque.investfund.fakes;

import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.adapters.ExchangeRepository;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.application.adapters.StatisticRepository;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DealResult;
import ru.ioque.investfund.domain.scanner.financial.entity.FinInstrument;
import ru.ioque.investfund.domain.scanner.financial.entity.TimeSeriesValue;
import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class FakeFinInstrumentRepository implements FinInstrumentRepository {
    ExchangeRepository exchangeRepository;
    StatisticRepository statisticRepository;

    @Override
    public List<FinInstrument> getAllByInstrumentIdIn(List<UUID> instrumentIds) {
        return instrumentIds.stream().map(id -> {
            Instrument instrument = exchangeRepository.get().getInstruments().stream().filter(row -> row.getId().equals(id)).findFirst().orElseThrow();
            InstrumentStatistic statistic = statisticRepository.getBy(id);
            return FinInstrument.builder()
                .instrumentId(instrument.getId())
                .ticker(instrument.getTicker())
                .historyMedianValue(statistic.getHistoryMedianValue())
                .todayLastPrice(statistic.getTodayLastPrice())
                .todayOpenPrice(statistic.getTodayOpenPrice())
                .todayValue(statistic.getTodayValue())
                .buyToSellValuesRatio(statistic.getBuyToSellValuesRatio())
                .waPriceSeries(instrument.getDailyValues()
                    .stream()
                    .filter(row -> row.getClass().equals(DealResult.class))
                    .map(DealResult.class::cast)
                    .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate())).toList()
                )
                .closePriceSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate())).toList())
                .openPriceSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate())).toList())
                .valueSeries(instrument.getDailyValues().stream().map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate())).toList())
                .todayPriceSeries(instrument.getIntradayValues().stream().map(intradayValue -> new TimeSeriesValue<>(intradayValue.getPrice(), intradayValue.getDateTime().toLocalTime())).toList())
                .build();
        }).toList();
    }
}
