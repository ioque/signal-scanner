package ru.ioque.investfund.fakes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.application.adapters.DatasourceRepository;
import ru.ioque.investfund.domain.datasource.entity.Datasource;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakeDatasourceRepository implements DatasourceRepository {
    final Map<UUID, Datasource> exchanges = new ConcurrentHashMap<>();
    final Map<UUID, Map<String, List<IntradayValue>>> intradayValues = new ConcurrentHashMap<>();
    final Map<UUID, Map<String, List<HistoryValue>>> historyValues = new ConcurrentHashMap<>();

    public Stream<IntradayValue> getIntradayBy(UUID datasourceId, String ticker) {
        return intradayValues
            .getOrDefault(datasourceId, new ConcurrentHashMap<>())
            .getOrDefault(ticker, new ArrayList<>())
            .stream();
    }

    public Stream<HistoryValue> getHistoryBy(UUID datasourceId, String ticker) {
        return historyValues
            .getOrDefault(datasourceId, new ConcurrentHashMap<>())
            .getOrDefault(ticker, new ArrayList<>())
            .stream();
    }

    @Override
    public List<Datasource> getAll() {
        return exchanges.values().stream().toList();
    }

    @Override
    public Optional<Datasource> getBy(UUID datasourceId) {
        return Optional.ofNullable(exchanges.get(datasourceId));
    }

    @Override
    public void saveDatasource(Datasource datasource) {
        exchanges.put(datasource.getId(), datasource);
    }

    @Override
    public void saveIntradayValues(List<IntradayValue> newValues) {
        Map<UUID, List<IntradayValue>> datasourceIdToValues = newValues
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

    @Override
    public void saveHistoryValues(List<HistoryValue> newValues) {
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
                LocalDate lastDate = currentIntraday.stream().max(HistoryValue::compareTo).map(HistoryValue::getTradeDate).orElse(null);
                currentIntraday.addAll(history
                    .stream()
                    .filter(row -> lastDate == null || row.getTradeDate().isAfter(lastDate))
                    .toList());
                currentTickerToValues.put(ticker, currentIntraday);
            });
        });
    }

    @Override
    public void deleteDatasource(UUID datasourceId) {
        exchanges.remove(datasourceId);
    }
}