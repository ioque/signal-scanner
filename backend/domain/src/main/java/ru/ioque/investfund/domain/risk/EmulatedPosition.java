package ru.ioque.investfund.domain.risk;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.scanner.entity.SignalScanner;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmulatedPosition {
    EmulatedPositionId id;
    SignalScanner scanner;
    Instrument instrument;
    Double openPrice;
    Double lastPrice;
    Double closePrice;
    Boolean isOpen;
    Double profit;

    public void updateLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
        evaluateProfit();
    }

    public void closePosition(Double closePrice) {
        this.closePrice = closePrice;
        this.lastPrice = closePrice;
        this.isOpen = false;
        evaluateProfit();
    }

    private void evaluateProfit() {
        profit = (lastPrice/openPrice - 1) * 100;
    }
}
