package ru.ioque.investfund.fakes;

import ru.ioque.investfund.application.adapters.ConfigureProvider;

public class FakeConfigureProvider implements ConfigureProvider {

    @Override
    public String exchangeName() {
        return "Московская биржа";
    }

    @Override
    public String exchangeDescription() {
        return "Московская биржа";
    }

    @Override
    public String exchangeServerUrl() {
        return "http://localhost:8081";
    }
}
