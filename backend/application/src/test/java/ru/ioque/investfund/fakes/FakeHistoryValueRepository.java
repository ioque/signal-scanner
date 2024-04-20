package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.HistoryValueRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeHistoryValueRepository implements HistoryValueRepository {
    final Map<InstrumentId, Set<HistoryValue>> historyValues = new ConcurrentHashMap<>();


    public Stream<HistoryValue> getAllBy(InstrumentId instrumentId) {
        return historyValues
            .getOrDefault(instrumentId, new HashSet<>())
            .stream();
    }

    @Override
    public void saveAll(List<HistoryValue> newValues) {
        Map<InstrumentId, List<HistoryValue>> instrumentIdToValues = newValues
            .stream()
            .collect(Collectors.groupingBy(HistoryValue::getInstrumentId));
        instrumentIdToValues.forEach((instrumentId, values) -> {
            if (!this.historyValues.containsKey(instrumentId)) {
                this.historyValues.put(instrumentId, new HashSet<>());
            }
            this.historyValues.get(instrumentId).addAll(values);
        });
    }
}
