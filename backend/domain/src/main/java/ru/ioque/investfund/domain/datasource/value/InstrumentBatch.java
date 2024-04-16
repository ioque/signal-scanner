package ru.ioque.investfund.domain.datasource.value;

import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.List;
import java.util.UUID;

public class InstrumentBatch {
    UUID datasourceId;
    List<Instrument> instruments;

    public InstrumentBatch(UUID datasourceId, List<Instrument> instruments) {
        this.datasourceId = datasourceId;
        this.instruments = instruments;
        if (instruments.stream().anyMatch(row -> !row.getDatasourceId().equals(datasourceId))) {
            throw new IllegalArgumentException("Instrument batch must contain instrument with same datasourceId.");
        }
    }

    public List<Instrument> getUniqueValues() {
        return instruments.stream().distinct().toList();
    }
}
