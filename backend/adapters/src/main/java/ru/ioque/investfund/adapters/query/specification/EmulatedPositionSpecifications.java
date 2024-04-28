package ru.ioque.investfund.adapters.query.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.ioque.investfund.adapters.persistence.entity.risk.EmulatedPositionEntity;

import java.util.UUID;

public class EmulatedPositionSpecifications {
    public static Specification<EmulatedPositionEntity> tickerLike(String ticker) {
        if (ticker == null || ticker.isEmpty()) throw new RuntimeException();
        return (emulatedPosition, cq, cb) -> cb.like(emulatedPosition.get("instrument").get("ticker"), "%" + ticker + "%");
    }

    public static Specification<EmulatedPositionEntity> scannerIdEqual(UUID scannerId) {
        if (scannerId == null) throw new RuntimeException();
        return (instrument, cq, cb) -> cb.equal(instrument.get("scanner").get("id"), scannerId);
    }
}
