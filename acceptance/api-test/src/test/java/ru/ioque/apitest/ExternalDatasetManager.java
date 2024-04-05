package ru.ioque.apitest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.core.client.datasource.DatasourceClient;
import ru.ioque.core.dataset.Dataset;

@Component
@Profile("!local")
public class ExternalDatasetManager implements DatasetManager {
    private final DatasourceClient datasourceClient;

    public ExternalDatasetManager(@Value("${variables.datasource_url}") String datasourceUrl) {
        datasourceClient = new DatasourceClient(datasourceUrl);
    }

    @Override
    public void initDataset(Dataset dataset) {
        datasourceClient.initDataset(dataset);
    }
}
