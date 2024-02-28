package ru.ioque.investfund.adapters.exchange.emulator;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.util.List;

// Данные инициализируются через генератор случайных значений, на базе исторических данных или из excel
// Достаточно загрузить массив данных, а система тестирования сэмулирует поток данных, сдвигая программно время системы.
@Component
@Profile("test")
public class EmulatorExchangeProvider implements ExchangeProvider {
    @Override
    public List<DailyValue> fetchDailyTradingResultsBy(Instrument instrument) {
        return null;
    }

    @Override
    public List<IntradayValue> fetchIntradayValuesBy(Instrument instrument) {
        return null;
    }

    @Override
    public List<Instrument> fetchInstruments() {
        return null;
    }
}
