package ru.ioque.uiseeder;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ioque.core.client.datasource_provider.DatasourceProviderClient;
import ru.ioque.core.client.datasource.DatasourceRestClient;
import ru.ioque.core.client.service.ServiceClient;
import ru.ioque.core.client.signalscanner.SignalScannerRestClient;
import ru.ioque.core.client.archive.ArchiveRestClient;

@Getter
@Component
public class ClientFacade {
    private final DatasourceRestClient datasourceRestClient;
    private final ArchiveRestClient archiveRestClient;
    private final SignalScannerRestClient signalScannerRestClient;
    private final ServiceClient serviceClient;
    private final DatasourceProviderClient datasourceProviderClient;
    public ClientFacade(
        @Value("${variables.api_url}") String apiUrl,
        @Value("${variables.datasource_url}") String datasourceUrl
        ) {
        datasourceRestClient = new DatasourceRestClient(apiUrl);
        archiveRestClient = new ArchiveRestClient(apiUrl);
        signalScannerRestClient = new SignalScannerRestClient(apiUrl);
        serviceClient = new ServiceClient(apiUrl);
        datasourceProviderClient = new DatasourceProviderClient(datasourceUrl);
    }
}
