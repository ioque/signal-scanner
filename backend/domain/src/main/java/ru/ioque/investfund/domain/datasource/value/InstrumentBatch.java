package ru.ioque.investfund.domain.datasource.value;

import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.indetity.DatasourceId;

import java.util.List;

public class InstrumentBatch {
    DatasourceId datasourceId;
    List<Instrument> instruments;

    public InstrumentBatch(DatasourceId datasourceId, List<Instrument> instruments) {
        this.datasourceId = datasourceId;
        this.instruments = instruments;
        if (instruments.stream().anyMatch(row -> !row.getId().getDatasourceId().equals(datasourceId))) {
            throw new IllegalArgumentException("Instrument batch must contain instrument with same datasourceId.");
        }
    }

    public List<Instrument> getUniqueValues() {
        return instruments.stream().distinct().toList();
    }
}
