package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeIntradayValueRepository implements IntradayValueRepository {
    final Map<DatasourceId, Map<String, List<IntradayValue>>> intradayValues = new ConcurrentHashMap<>();

    public Stream<IntradayValue> getAllBy(DatasourceId datasourceId) {
        return intradayValues
            .getOrDefault(datasourceId, new ConcurrentHashMap<>())
            .values()
            .stream()
            .flatMap(Collection::stream);
    }

    public Stream<IntradayValue> getAllBy(DatasourceId datasourceId, String ticker) {
        return intradayValues
            .getOrDefault(datasourceId, new ConcurrentHashMap<>())
            .getOrDefault(ticker, new ArrayList<>())
            .stream();
    }

    @Override
    public void saveAll(List<IntradayValue> newValues) {
        Map<DatasourceId, List<IntradayValue>> datasourceIdToValues = newValues
            .stream()
            .collect(Collectors.groupingBy(IntradayValue::getDatasourceId));
        datasourceIdToValues.forEach((datasourceId, values) -> {
            if (!this.intradayValues.containsKey(datasourceId)) {
                this.intradayValues.put(datasourceId, new ConcurrentHashMap<>());
            }
            Map<String, List<IntradayValue>> currentTickerToValues = this.intradayValues.get(datasourceId);
            Map<String, List<IntradayValue>> newTickerToValues = values
                .stream()
                .collect(Collectors.groupingBy(IntradayValue::getTicker));
            newTickerToValues.forEach((ticker, intraday) -> {
                List<IntradayValue> currentIntraday = currentTickerToValues.getOrDefault(ticker, new ArrayList<>());
                Long lastNumber = currentIntraday.stream().mapToLong(IntradayValue::getNumber).max().orElse(0);
                currentIntraday.addAll(intraday.stream().filter(row -> row.getNumber() > lastNumber).toList());
                currentTickerToValues.put(ticker, currentIntraday);
            });
        });
    }
}
