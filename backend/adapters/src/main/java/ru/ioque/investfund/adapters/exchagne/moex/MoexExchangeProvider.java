package ru.ioque.investfund.adapters.exchagne.moex;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.ioque.investfund.adapters.exchagne.moex.client.MoexRestClient;
import ru.ioque.investfund.adapters.exchagne.moex.client.dto.InstrumentDto;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.application.adapters.ExchangeProvider;
import ru.ioque.investfund.application.adapters.UUIDProvider;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Futures;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.FuturesDeal;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MoexExchangeProvider implements ExchangeProvider {
    MoexRestClient moexClient;
    DateTimeProvider dateTimeProvider;
    UUIDProvider uuidProvider;

    @Override
    @SneakyThrows
    public List<DailyValue> fetchDailyTradingResultsBy(
        Instrument instrument
    ) {
        return moexClient.fetchDailyTradingResults(
            instrument,
            from(instrument),
            till()
        );
    }

    @Override
    @SneakyThrows
    public List<IntradayValue> fetchIntradayValuesBy(Instrument instrument) {
        List<IntradayValue> intradayValues = moexClient.fetchIntradayValues(instrument);
        if (instrument.getClass().equals(Futures.class)) {
            intradayValues = intradayValues
                .stream()
                .map(row -> (IntradayValue) FuturesDeal.builder()
                    .dateTime(row.getDateTime())
                    .ticker(row.getTicker())
                    .qnt(((FuturesDeal) row).getQnt())
                    .price(row.getPrice())
                    .number(row.getNumber())
                    .value(row.getPrice() * ((FuturesDeal) row).getQnt() * ((Futures) instrument).getLotVolume())
                    .build())
                .toList();
        }
        return intradayValues;
    }

    @Override
    @SneakyThrows
    public List<Instrument> fetchInstruments() {
        List<InstrumentDto> instruments = new ArrayList<>();
        instruments.addAll(moexClient.fetchInstruments(Stock.class));
        instruments.addAll(moexClient.fetchInstruments(CurrencyPair.class));
        instruments.addAll(moexClient.fetchInstruments(Futures.class));
        instruments.addAll(moexClient.fetchInstruments(Index.class));
        return instruments.stream().map(dto -> dto.toDomain(uuidProvider.generate())).toList();
    }

    //TODO унести в ExchangeManager, сделать параметрами ExchangeProvider
    private LocalDate till() {
        return dateTimeProvider.daysAgo(1);
    }

    private LocalDate from(Instrument instrument) {
        return instrument
            .lastDailyValue()
            .map(DailyValue::getTradeDate)
            .orElse(dateTimeProvider.monthsAgo(6));
    }
}
