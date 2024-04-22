package ru.ioque.uiseeder;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ioque.core.client.datasource_provider.DatasourceProviderHttpClient;
import ru.ioque.core.client.datasource.DatasourceHttpClient;
import ru.ioque.core.client.service.ServiceHttpClient;
import ru.ioque.core.client.signalscanner.ScannerHttpClient;
import ru.ioque.core.client.archive.ArchiveHttpClient;

@Getter
@Component
public class ClientFacade {
    private final DatasourceHttpClient datasourceRestClient;
    private final ArchiveHttpClient archiveRestClient;
    private final ScannerHttpClient signalScannerRestClient;
    private final ServiceHttpClient serviceClient;
    private final DatasourceProviderHttpClient datasourceProviderClient;
    public ClientFacade(
        @Value("${variables.api_url}") String apiUrl,
        @Value("${variables.datasource_url}") String datasourceUrl
        ) {
        datasourceRestClient = new DatasourceHttpClient(apiUrl);
        archiveRestClient = new ArchiveHttpClient(apiUrl);
        signalScannerRestClient = new ScannerHttpClient(apiUrl);
        serviceClient = new ServiceHttpClient(apiUrl);
        datasourceProviderClient = new DatasourceProviderHttpClient(datasourceUrl);
    }
}
