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

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FakeDatasourceRepository implements DatasourceRepository {
    final Map<UUID, Datasource> exchanges = new ConcurrentHashMap<>();
    @Getter
    final Map<String, List<IntradayValue>> intradayValues = new ConcurrentHashMap<>();
    @Getter
    final Map<String, List<HistoryValue>> historyValues = new ConcurrentHashMap<>();

    @Override
    public Optional<Datasource> get() {
        return exchanges.values().stream().findFirst();
    }

    @Override
    public void saveDatasource(Datasource datasource) {
        exchanges.put(datasource.getId(), datasource);
    }

    @Override
    public void saveIntradayValues(List<IntradayValue> newValues) {
        Map<String, List<IntradayValue>> grouped =
            newValues.stream().collect(Collectors.groupingBy(IntradayValue::getTicker));
        grouped.forEach((key, value) -> {
            List<IntradayValue> current = this.intradayValues.getOrDefault(key, new ArrayList<>());
            Long lastNumber = current.stream().mapToLong(IntradayValue::getNumber).max().orElse(0);
            current.addAll(value.stream().filter(row -> row.getNumber() > lastNumber).toList());
            intradayValues.put(key, current);
        });
    }

    @Override
    public void saveHistoryValues(List<HistoryValue> newValues) {
        Map<String, List<HistoryValue>> grouped =
            newValues.stream().collect(Collectors.groupingBy(HistoryValue::getTicker));
        grouped.forEach((key, value) -> {
            List<HistoryValue> current = this.historyValues.getOrDefault(key, new ArrayList<>());
            LocalDate lastDate = current.stream().max(HistoryValue::compareTo).map(HistoryValue::getTradeDate).orElse(
                null);
            current.addAll(value
                .stream()
                .filter(row -> lastDate == null || row.getTradeDate().isAfter(lastDate))
                .toList());
            historyValues.put(key, current);
        });
    }

    @Override
    public void deleteDatasource() {
        exchanges.clear();
    }
}