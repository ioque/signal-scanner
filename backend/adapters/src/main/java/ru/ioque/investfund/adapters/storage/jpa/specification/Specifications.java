package ru.ioque.investfund.adapters.storage.jpa.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;

public class Specifications {
    public static Specification<InstrumentEntity> typeSpecification(Class<? extends InstrumentEntity> clazz) {
        return (instrument, cq, cb) -> cb.equal(instrument.type(), clazz);
    }

    public static Specification<InstrumentEntity> shortNameSpecification(Class<? extends InstrumentEntity> clazz) {
        return (instrument, cq, cb) -> cb.equal(instrument.get("shortName"), clazz);
    }

    public static Specification<InstrumentEntity> nameSpecification(Class<? extends InstrumentEntity> clazz) {
        return (instrument, cq, cb) -> cb.equal(instrument.get("name"), clazz);
    }

    public static Specification<InstrumentEntity> tickerSpecification(Class<? extends InstrumentEntity> clazz) {
        return (instrument, cq, cb) -> cb.equal(instrument.get("ticker"), clazz);
    }
}
