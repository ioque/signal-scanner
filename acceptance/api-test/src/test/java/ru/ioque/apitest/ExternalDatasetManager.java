package ru.ioque.apitest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.core.client.datasource_provider.DatasourceProviderClient;
import ru.ioque.core.dataset.Dataset;

@Component
@Profile("stage")
public class ExternalDatasetManager implements DatasetManager {
    private final DatasourceProviderClient datasourceProviderClient;

    public ExternalDatasetManager(@Value("${variables.datasource_url}") String datasourceUrl) {
        datasourceProviderClient = new DatasourceProviderClient(datasourceUrl);
    }

    @Override
    public void initDataset(Dataset dataset) {
        datasourceProviderClient.initDataset(dataset);
    }
}
