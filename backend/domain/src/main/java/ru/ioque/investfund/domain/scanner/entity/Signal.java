package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.event.SignalAutoClosed;
import ru.ioque.investfund.domain.scanner.entity.event.SignalEvent;
import ru.ioque.investfund.domain.scanner.entity.event.SignalRegistered;
import ru.ioque.investfund.domain.scanner.entity.event.TakingProfitBySignal;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.identifier.SignalId;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Signal implements Comparable<Signal> {
    @Setter
    SignalId id;
    @Setter
    ScannerId scannerId;
    InstrumentId instrumentId;
    Double openPrice;
    Double closePrice;
    Double fixedProfit;
    String summary;
    Instant timestamp;

    public boolean sameByInstrumentId(Signal signal) {
        return signal.getInstrumentId().equals(this.getInstrumentId());
    }

    public boolean sameByScannerId(Signal signal) {
        return signal.getScannerId().equals(this.getScannerId());
    }

    @Override
    public int compareTo(Signal signal) {
        return timestamp.compareTo(signal.timestamp);
    }

    public void applyEvents(List<SignalEvent> events) {
        for (final SignalEvent event : events) {
            idEquals(event.getSignalId());
            if (event instanceof SignalRegistered registered) {
                this.scannerId = registered.getScannerId();
                this.instrumentId = registered.getInstrumentId();
                this.openPrice = registered.getOpenPrice();
                this.summary = registered.getSummary();
                this.timestamp = registered.getTimestamp();
            }
            if (event instanceof SignalAutoClosed closed) {
                close(closed.getClosePrice());
            }
            if (event instanceof TakingProfitBySignal takingProfit) {
                close(takingProfit.getClosePrice());
            }
        }
    }

    private void idEquals(SignalId id) {
        if (!id.equals(this.id)) {
            throw new DomainException("signal id mismatch");
        }
    }

    public void close(Double closePrice) {
        this.closePrice = closePrice;
        this.fixedProfit = evaluateProfit(closePrice);
    }

    public Double evaluateProfit(Double price) {
        return (price/openPrice - 1) * 100;
    }
}
