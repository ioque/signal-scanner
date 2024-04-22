package ru.ioque.apitest.dataset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.core.client.datasource_provider.DatasourceProviderHttpClient;
import ru.ioque.core.dataset.Dataset;

@Component
@Profile("staging")
public class ExternalDatasetManager implements DatasetManager {
    private final DatasourceProviderHttpClient datasourceProviderClient;

    public ExternalDatasetManager(@Value("${variables.datasource_url}") String datasourceUrl) {
        datasourceProviderClient = new DatasourceProviderHttpClient(datasourceUrl);
    }

    @Override
    public void initDataset(Dataset dataset) {
        datasourceProviderClient.initDataset(dataset);
    }
}
