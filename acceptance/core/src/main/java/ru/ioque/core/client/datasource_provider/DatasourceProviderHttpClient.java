package ru.ioque.core.client.datasource_provider;

import lombok.SneakyThrows;
import ru.ioque.core.client.JsonHttpClient;
import ru.ioque.core.dataset.Dataset;

public class DatasourceProviderHttpClient extends JsonHttpClient {
    public DatasourceProviderHttpClient(String datasourceUrl) {
        super(datasourceUrl);
    }

    @SneakyThrows
    public void initDataset(Dataset dataset) {
        post("/api/dataset", objectMapper.writeValueAsString(dataset));
    }
}
