package ru.ioque.apitest;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
    public ClientFacade(@Value("${variables.api_url}") String apiHost) {
        datasourceRestClient = new DatasourceRestClient(apiHost);
        archiveRestClient = new ArchiveRestClient(apiHost);
        signalScannerRestClient = new SignalScannerRestClient(apiHost);
        serviceClient = new ServiceClient(apiHost);
    }
}
