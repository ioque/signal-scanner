package ru.ioque.investfund.adapters.query.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;

import java.util.UUID;

public class InstrumentSpecifications {
    public static Specification<InstrumentEntity> typeEqual(InstrumentType type) {
        return (instrument, cq, cb) -> cb.equal(instrument.get("type"), type);
    }

    public static Specification<InstrumentEntity> shortNameLike(String shortName) {
        if (shortName == null || shortName.isEmpty()) throw new RuntimeException();
        return (instrument, cq, cb) -> cb.like(instrument.get("details").get("shortName"), "%" + shortName + "%");
    }

    public static Specification<InstrumentEntity> tickerLike(String ticker) {
        if (ticker == null || ticker.isEmpty()) throw new RuntimeException();
        return (instrument, cq, cb) -> cb.like(instrument.get("ticker"), "%" + ticker + "%");
    }

    public static Specification<InstrumentEntity> datasourceIdEqual(UUID datasourceId) {
        if (datasourceId == null) throw new RuntimeException();
        return (instrument, cq, cb) -> cb.equal(instrument.get("datasource").get("id"), datasourceId);
    }
}
