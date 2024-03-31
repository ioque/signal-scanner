package ru.ioque.moexdatasource.adapters.rest.response.instrument;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.ioque.moexdatasource.domain.instrument.CurrencyPair;
import ru.ioque.moexdatasource.domain.instrument.Futures;
import ru.ioque.moexdatasource.domain.instrument.Index;
import ru.ioque.moexdatasource.domain.instrument.Instrument;
import ru.ioque.moexdatasource.domain.instrument.Stock;

import java.util.Map;
import java.util.function.Function;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class InstrumentResponse {
    String ticker;
    String shortName;
    String name;

    public static InstrumentResponse from(Instrument instrument) {
        return factoryMethods.get(instrument.getClass()).apply(instrument);
    }

    static Map<Class<? extends Instrument>, Function<Instrument, InstrumentResponse>> factoryMethods = Map.of(
        Stock.class, instrument -> StockResponse.from((Stock) instrument),
        Futures.class, instrument -> FuturesResponse.from((Futures) instrument),
        CurrencyPair.class, instrument -> CurrencyPairResponse.from((CurrencyPair) instrument),
        Index.class, instrument -> IndexResponse.from((Index) instrument)
    );
}
