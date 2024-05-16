package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeIntradayValueRepository implements IntradayValueRepository {
    final Map<Ticker, Set<IntradayData>> intradayValues = new ConcurrentHashMap<>();

    public Stream<IntradayData> getAllBy(Ticker ticker) {
        return intradayValues
            .getOrDefault(ticker, new HashSet<>())
            .stream();
    }
    @Override
    public void saveAll(Collection<IntradayData> newValues) {
        Map<Ticker, List<IntradayData>> tickerToValues = newValues
            .stream()
            .collect(Collectors.groupingBy(IntradayData::getTicker));
        tickerToValues.forEach((ticker, values) -> {
            if (!this.intradayValues.containsKey(ticker)) {
                this.intradayValues.put(ticker, new HashSet<>());
            }
            this.intradayValues.get(ticker).addAll(values);
        });
    }
}
