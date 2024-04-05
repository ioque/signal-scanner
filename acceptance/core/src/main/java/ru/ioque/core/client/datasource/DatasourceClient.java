package ru.ioque.core.client.datasource;

import lombok.SneakyThrows;
import ru.ioque.core.client.JsonApplicationHttpClient;
import ru.ioque.core.dataset.Dataset;

public class DatasourceClient extends JsonApplicationHttpClient {
    public DatasourceClient(String datasourceUrl) {
        super(datasourceUrl);
    }

    @SneakyThrows
    public void initDataset(Dataset dataset) {
        post("/api/dataset", objectMapper.writeValueAsString(dataset));
    }
}
