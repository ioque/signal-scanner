package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeIntradayValueRepository implements IntradayValueRepository {
    final Map<InstrumentId, Set<IntradayValue>> intradayValues = new ConcurrentHashMap<>();

    public Stream<IntradayValue> getAllBy(InstrumentId instrumentId) {
        return intradayValues
            .getOrDefault(instrumentId, new HashSet<>())
            .stream();
    }

    @Override
    public void saveAll(List<IntradayValue> newValues) {
        Map<InstrumentId, List<IntradayValue>> instrumentIdToValues = newValues
            .stream()
            .collect(Collectors.groupingBy(IntradayValue::getInstrumentId));
        instrumentIdToValues.forEach((instrumentId, values) -> {
            if (!this.intradayValues.containsKey(instrumentId)) {
                this.intradayValues.put(instrumentId, new HashSet<>());
            }
            this.intradayValues.get(instrumentId).addAll(values);
        });
    }
}
