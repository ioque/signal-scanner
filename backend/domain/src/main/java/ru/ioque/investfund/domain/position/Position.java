package ru.ioque.investfund.domain.position;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.Signal;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Position {
    PositionId id;
    ScannerId scannerId;
    InstrumentId instrumentId;
    Double openPrice;
    Double closePrice;
    Boolean isOpen;

    public static Position from(Signal signal) {
        return Position.builder()
            .scannerId(signal.getScannerId())
            .instrumentId(signal.getInstrumentId())
            .openPrice(signal.getPrice())
            .isOpen(true)
            .build();
    }

    public void closePosition(Double closePrice) {
        this.closePrice = closePrice;
        this.isOpen = false;
    }

    public Double evaluateProfit(Double price) {
        return (price/openPrice - 1) * 100;
    }
}
