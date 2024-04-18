package ru.ioque.investfund.domain.datasource.value;

import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;

public class InstrumentBatch {
    List<Instrument> instruments;

    public InstrumentBatch(List<Instrument> instruments) {
        this.instruments = instruments;
    }

    public List<Instrument> getUniqueValues() {
        return instruments.stream().distinct().toList();
    }
}
