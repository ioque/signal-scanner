package ru.ioque.apitest.modules.datasource;

import ru.ioque.apitest.DatasourceEmulatedTest;
import ru.ioque.core.datagenerator.history.HistoryValue;
import ru.ioque.core.datagenerator.instrument.Instrument;
import ru.ioque.core.datagenerator.intraday.IntradayValue;
import ru.ioque.core.dataset.Dataset;
import ru.ioque.core.dto.datasource.request.DatasourceRequest;

import java.util.List;
import java.util.UUID;

public class DatasourceAcceptanceTest extends DatasourceEmulatedTest {
    protected void removeDatasource(UUID datasourceId) {
        datasourceClient().removeDatasource(datasourceId);
    }

    protected void updateDatasource(UUID datasourceId, DatasourceRequest request) {
        datasourceClient().updateDatasource(datasourceId, request);
    }

    protected void createDatasource(DatasourceRequest request) {
        datasourceClient().createDatasource(request);
    }

    protected void initInstruments(List<Instrument> instruments) {
        initDataset(Dataset.builder().instruments(instruments).build());
    }

    protected void initDataset(
        List<Instrument> instruments,
        List<HistoryValue> historyValues,
        List<IntradayValue> intradayValues
    ) {
        initDataset(Dataset
            .builder()
            .instruments(instruments)
            .historyValues(historyValues)
            .intradayValues(intradayValues)
            .build());
    }

    protected UUID getFirstDatasourceId() {
        return getAllDatasource().get(0).getId();
    }
}
