package ru.ioque.investfund.adapters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.ConfigureProvider;

@Component
public class SpringConfigProvider implements ConfigureProvider {
    @Value("${exchange.name}")
    private String exchangeName;
    @Value("${exchange.description}")
    private String exchangeDescription;
    @Value("${exchange.url}")
    private String exchangeServerUrl;


    @Override
    public String exchangeName() {
        return exchangeName;
    }

    @Override
    public String exchangeDescription() {
        return exchangeDescription;
    }

    @Override
    public String exchangeServerUrl() {
        return exchangeServerUrl;
    }
}
