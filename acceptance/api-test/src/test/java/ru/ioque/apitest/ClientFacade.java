package ru.ioque.apitest;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ioque.core.client.exchange.ExchangeRestClient;
import ru.ioque.core.client.service.ServiceClient;
import ru.ioque.core.client.signalscanner.SignalScannerRestClient;
import ru.ioque.core.client.testingsystem.TestingSystemRestClient;

@Getter
@Component
public class ClientFacade {
    private final ExchangeRestClient exchangeRestClient;
    private final TestingSystemRestClient testingSystemRestClient;
    private final SignalScannerRestClient signalScannerRestClient;
    private final ServiceClient serviceClient;
    public ClientFacade(@Value("${variables.api_host}") String apiHost) {
        exchangeRestClient = new ExchangeRestClient(apiHost);
        testingSystemRestClient = new TestingSystemRestClient(apiHost);
        signalScannerRestClient = new SignalScannerRestClient(apiHost);
        serviceClient = new ServiceClient(apiHost);
    }
}
