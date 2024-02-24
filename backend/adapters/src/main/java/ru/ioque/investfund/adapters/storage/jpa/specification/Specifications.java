package ru.ioque.investfund.adapters.storage.jpa.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.CurrencyPairEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.FuturesEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.IndexEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.StockEntity;

import java.util.Map;

public class Specifications {
    public static Specification<InstrumentEntity> typeEqual(String type) {
        Map<String, Class<? extends InstrumentEntity>> typeToClass = Map.of(
            "stock", StockEntity.class,
            "futures", FuturesEntity.class,
            "currencyPair", CurrencyPairEntity.class,
            "index", IndexEntity.class
        );

        if (!typeToClass.containsKey(type)) {
            throw new RuntimeException();
        }

        return (instrument, cq, cb) -> cb.equal(instrument.type(), typeToClass.get(type));
    }

    public static Specification<InstrumentEntity> shortNameLike(String shortName) {
        if (shortName == null || shortName.isEmpty()) throw new RuntimeException();
        return (instrument, cq, cb) -> cb.like(instrument.get("shortName"), "%" + shortName + "%");
    }

    public static Specification<InstrumentEntity> tickerLike(String ticker) {
        if (ticker == null || ticker.isEmpty()) throw new RuntimeException();
        return (instrument, cq, cb) -> cb.like(instrument.get("ticker"), "%" + ticker + "%");
    }
}
