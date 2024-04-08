package ru.ioque.core.client.datasource_provider;

import lombok.SneakyThrows;
import ru.ioque.core.client.JsonApplicationHttpClient;
import ru.ioque.core.dataset.Dataset;

public class DatasourceProviderClient extends JsonApplicationHttpClient {
    public DatasourceProviderClient(String datasourceUrl) {
        super(datasourceUrl);
    }

    @SneakyThrows
    public void initDataset(Dataset dataset) {
        post("/api/dataset", objectMapper.writeValueAsString(dataset));
    }
}
