package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeHistoryValueRepository implements HistoryValueRepository {
    final Map<UUID, Map<String, List<HistoryValue>>> historyValues = new ConcurrentHashMap<>();

    public Stream<HistoryValue> getAllBy(UUID datasourceId) {
        return historyValues
            .getOrDefault(datasourceId, new ConcurrentHashMap<>())
            .values()
            .stream()
            .flatMap(Collection::stream);
    }

    public Stream<HistoryValue> getAllBy(UUID datasourceId, String ticker) {
        return historyValues
            .getOrDefault(datasourceId, new ConcurrentHashMap<>())
            .getOrDefault(ticker, new ArrayList<>())
            .stream();
    }

    @Override
    public void saveAll(List<HistoryValue> newValues) {
        Map<UUID, List<HistoryValue>> datasourceIdToValues = newValues
            .stream()
            .collect(Collectors.groupingBy(HistoryValue::getDatasourceId));
        datasourceIdToValues.forEach((datasourceId, values) -> {
            if (!this.historyValues.containsKey(datasourceId)) {
                this.historyValues.put(datasourceId, new ConcurrentHashMap<>());
            }
            Map<String, List<HistoryValue>> currentTickerToValues = this.historyValues.get(datasourceId);
            Map<String, List<HistoryValue>> newTickerToValues = values
                .stream()
                .collect(Collectors.groupingBy(HistoryValue::getTicker));
            newTickerToValues.forEach((ticker, history) -> {
                List<HistoryValue> currentIntraday = currentTickerToValues.getOrDefault(ticker, new ArrayList<>());
                LocalDate
                    lastDate = currentIntraday.stream().max(HistoryValue::compareTo).map(HistoryValue::getTradeDate).orElse(null);
                currentIntraday.addAll(history
                    .stream()
                    .filter(row -> lastDate == null || row.getTradeDate().isAfter(lastDate))
                    .toList());
                currentTickerToValues.put(ticker, currentIntraday);
            });
        });
    }
}
