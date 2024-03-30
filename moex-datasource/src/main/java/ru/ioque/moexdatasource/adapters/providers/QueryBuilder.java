package ru.ioque.moexdatasource.adapters.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.ioque.moexdatasource.domain.instrument.CurrencyPair;
import ru.ioque.moexdatasource.domain.instrument.Futures;
import ru.ioque.moexdatasource.domain.instrument.Index;
import ru.ioque.moexdatasource.domain.instrument.Instrument;
import ru.ioque.moexdatasource.domain.instrument.Stock;

import java.time.LocalDate;
import java.util.Map;

@Component
public class QueryBuilder {
    @Value("${exchange.url}")
    private String apiServer;

    public String getSecurityListQuery(Class<? extends Instrument> type) {
        return url(typeToListUrl.get(type));
    }

    public String getFetchDealBy(Class<? extends Instrument> type, String ticker) {
        return url(String.format(typeToDealUrl.get(type), ticker));
    }

    public String fetchTradingResultsBy(Class<? extends Instrument> type, String ticker, LocalDate from, LocalDate till) {
        return url(String.format(typeToHistoryUrl.get(type), ticker, from, till));
    }

    private String url(String path) {
        return apiServer + path;
    }


    private final Map<Class<? extends Instrument>, String> typeToListUrl = Map.of(
        Stock.class, "/iss/engines/stock/markets/shares/boards/TQBR/securities.json", //248 инструментов
        Futures.class, "/iss/engines/futures/markets/forts/boards/RFUD/securities.json", //464 инструмента
        Index.class, "/iss/engines/stock/markets/index/boards/SNDX/securities.json", //68 инструментов
        CurrencyPair.class, "/iss/engines/currency/markets/selt/boards/CETS/securities.json" //191, но есть без тикеров, надо фильтровать говно
    );
    private final Map<Class<? extends Instrument>, String> typeToDealUrl = Map.of(
        Stock.class, "/iss/engines/stock/markets/shares/boards/TQBR/securities/%s/trades.json?",
        Futures.class, "/iss/engines/futures/markets/forts/boards/RFUD/securities/%s/trades.json?",
        Index.class, "/iss/engines/stock/markets/index/boards/SNDX/securities/%s/trades.json?",
        CurrencyPair.class, "/iss/engines/currency/markets/selt/boards/CETS/securities/%s/trades.json?"
    );

    private final Map<Class<? extends Instrument>, String> typeToHistoryUrl = Map.of(
        Stock.class, "/iss/history/engines/stock/markets/shares/boards/TQBR/securities/%s.json?from=%s&till=%s&",
        Futures.class, "/iss/history/engines/futures/markets/forts/boards/RFUD/securities/%s.json?from=%s&till=%s&",
        Index.class, "/iss/history/engines/stock/markets/index/boards/SNDX/securities/%s.json?from=%s&till=%s&",
        CurrencyPair.class, "/iss/history/engines/currency/markets/selt/boards/CETS/securities/%s.json?from=%s&till=%s&"
    );
}
