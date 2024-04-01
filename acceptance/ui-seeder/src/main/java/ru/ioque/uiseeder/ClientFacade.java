package ru.ioque.uiseeder;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ioque.core.client.exchange.ExchangeRestClient;
import ru.ioque.core.client.service.ServiceClient;
import ru.ioque.core.client.signalscanner.SignalScannerRestClient;
import ru.ioque.core.client.archive.ArchiveRestClient;

@Getter
@Component
public class ClientFacade {
    private final ExchangeRestClient exchangeRestClient;
    private final ArchiveRestClient archiveRestClient;
    private final SignalScannerRestClient signalScannerRestClient;
    private final ServiceClient serviceClient;
    public ClientFacade(@Value("${variables.api_url}") String apiUrl) {
        exchangeRestClient = new ExchangeRestClient(apiUrl);
        archiveRestClient = new ArchiveRestClient(apiUrl);
        signalScannerRestClient = new SignalScannerRestClient(apiUrl);
        serviceClient = new ServiceClient(apiUrl);
    }
}
