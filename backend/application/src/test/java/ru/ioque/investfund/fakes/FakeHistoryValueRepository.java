package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.domain.datasource.value.history.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeHistoryValueRepository implements HistoryValueRepository {
    final Map<Ticker, Set<HistoryValue>> historyValues = new ConcurrentHashMap<>();


    public Stream<HistoryValue> getAllBy(Ticker ticker) {
        return historyValues
            .getOrDefault(ticker, new HashSet<>())
            .stream();
    }

    @Override
    public void saveAll(List<HistoryValue> newValues) {
        Map<Ticker, List<HistoryValue>> tickerToValues = newValues
            .stream()
            .collect(Collectors.groupingBy(HistoryValue::getTicker));
        tickerToValues.forEach((ticker, values) -> {
            if (!this.historyValues.containsKey(ticker)) {
                this.historyValues.put(ticker, new HashSet<>());
            }
            this.historyValues.get(ticker).addAll(values);
        });
    }
}
