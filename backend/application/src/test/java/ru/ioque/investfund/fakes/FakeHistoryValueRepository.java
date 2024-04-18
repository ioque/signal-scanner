package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeHistoryValueRepository implements HistoryValueRepository {
    final Map<InstrumentId, List<HistoryValue>> historyValues = new ConcurrentHashMap<>();

    public Stream<HistoryValue> getAllBy(InstrumentId instrumentId) {
        return historyValues.getOrDefault(instrumentId, List.of()).stream();
    }

    @Override
    public void saveAll(List<HistoryValue> newValues) {
        Map<InstrumentId, List<HistoryValue>> groupedValues = newValues
            .stream()
            .collect(Collectors.groupingBy(HistoryValue::getInstrumentId));
        groupedValues.forEach((instrumentId, values) -> {
            if (!historyValues.containsKey(instrumentId)) {
                historyValues.put(instrumentId, new ArrayList<>());
            }
            historyValues.get(instrumentId).addAll(values);
        });
    }
}
