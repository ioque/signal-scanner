package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.IntradayValueRepository;
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeIntradayValueRepository implements IntradayValueRepository {
    final Map<InstrumentId, List<IntradayValue>> intradayValues = new ConcurrentHashMap<>();

    public Stream<IntradayValue> getAllBy(InstrumentId instrumentId) {
        return intradayValues.getOrDefault(instrumentId, List.of()).stream();
    }

    @Override
    public void saveAll(List<IntradayValue> newValues) {
        Map<InstrumentId, List<IntradayValue>> groupedValues = newValues
            .stream()
            .collect(Collectors.groupingBy(IntradayValue::getInstrumentId));
        groupedValues.forEach((instrumentId, values) -> {
            if (!intradayValues.containsKey(instrumentId)) {
                intradayValues.put(instrumentId, new ArrayList<>());
            }
            intradayValues.get(instrumentId).addAll(values);
        });
    }
}
