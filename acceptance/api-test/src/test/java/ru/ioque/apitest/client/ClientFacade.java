package ru.ioque.apitest.client;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    public ClientFacade(@Value("${variables.api_url}") String apiHost) {
        datasourceRestClient = new DatasourceHttpClient(apiHost);
        archiveRestClient = new ArchiveHttpClient(apiHost);
        signalScannerRestClient = new ScannerHttpClient(apiHost);
        serviceClient = new ServiceHttpClient(apiHost);
    }
}
