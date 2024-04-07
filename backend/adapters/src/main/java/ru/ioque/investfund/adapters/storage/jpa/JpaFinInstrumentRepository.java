package ru.ioque.investfund.adapters.storage.jpa;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.repositories.HistoryValueEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.InstrumentEntityRepository;
import ru.ioque.investfund.adapters.storage.jpa.repositories.IntradayValueEntityRepository;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.FinInstrumentRepository;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;
import ru.ioque.investfund.domain.scanner.entity.TradingSnapshot;
import ru.ioque.investfund.domain.scanner.value.TimeSeriesValue;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class JpaFinInstrumentRepository implements FinInstrumentRepository {
    InstrumentEntityRepository instrumentEntityRepository;
    HistoryValueEntityRepository historyValueEntityRepository;
    IntradayValueEntityRepository intradayValueEntityRepository;
    DateTimeProvider dateTimeProvider;

    @Override
    public List<TradingSnapshot> getBy(List<String> tickers) {
        return tickers
            .stream()
            .map(ticker -> {
                List<HistoryValue> historyValues = historyValueEntityRepository
                    .findAllBy(ticker, dateTimeProvider.nowDate().minusMonths(6))
                    .stream()
                    .map(HistoryValueEntity::toDomain)
                    .toList();
                List<IntradayValue> intradayValues = intradayValueEntityRepository
                    .findAllBy(ticker, dateTimeProvider.nowDate().atStartOfDay())
                    .stream()
                    .map(IntradayValueEntity::toDomain)
                    .toList();
                return TradingSnapshot.builder()
                    .ticker(ticker)
                    .waPriceSeries(historyValues
                        .stream()
                        .filter(row -> Objects.nonNull(row.getWaPrice()) && row.getWaPrice() > 0)
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getWaPrice(), dailyValue.getTradeDate()))
                        .toList()
                    )
                    .closePriceSeries(historyValues
                        .stream()
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getClosePrice(), dailyValue.getTradeDate()))
                        .toList())
                    .openPriceSeries(historyValues
                        .stream()
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getOpenPrice(), dailyValue.getTradeDate()))
                        .toList())
                    .valueSeries(historyValues
                        .stream()
                        .map(dailyValue -> new TimeSeriesValue<>(dailyValue.getValue(), dailyValue.getTradeDate()))
                        .toList())
                    .todayValueSeries(intradayValues
                        .stream()
                        .map(intradayValue -> new TimeSeriesValue<>(
                            intradayValue.getValue(),
                            intradayValue.getDateTime().toLocalTime()
                        ))
                        .toList()
                    )
                    .todayPriceSeries(intradayValues
                        .stream()
                        .map(intradayValue -> new TimeSeriesValue<>(
                            intradayValue.getPrice(),
                            intradayValue.getDateTime().toLocalTime()
                        ))
                        .toList())
                    .build();
            })
            .toList();
    }
}
