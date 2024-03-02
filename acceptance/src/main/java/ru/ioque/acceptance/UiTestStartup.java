package ru.ioque.acceptance;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.acceptance.adapters.client.exchange.ExchangeRestClient;
import ru.ioque.acceptance.adapters.client.exchange.request.EnableUpdateInstrumentRequest;
import ru.ioque.acceptance.domain.exchange.InstrumentInList;

@Component
@Profile("ui-test")
@AllArgsConstructor
public class UiTestStartup implements ApplicationListener<ApplicationReadyEvent> {
    ExchangeRestClient exchangeRestClient;
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        exchangeRestClient.synchronizeWithDataSource();
        exchangeRestClient
            .enableUpdateInstruments(
                new EnableUpdateInstrumentRequest(
                    exchangeRestClient
                        .getInstruments("")
                        .stream()
                        .map(InstrumentInList::getId)
                        .toList()
                )
            );
        exchangeRestClient.integrateTradingData();
    }
}
