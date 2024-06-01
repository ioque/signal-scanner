package ru.ioque.investfund.domain.scanner.entity.event;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.scanner.entity.identifier.ScannerId;
import ru.ioque.investfund.domain.scanner.entity.identifier.SignalId;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalRegistered extends SignalEvent {

    ScannerId scannerId;
    InstrumentId instrumentId;
    Double openPrice;
    String summary;

    @Builder
    public SignalRegistered(
        SignalId signalId,
        Instant timestamp,
        ScannerId scannerId,
        InstrumentId instrumentId,
        Double openPrice,
        String summary) {
        super(signalId, timestamp);
        this.scannerId = scannerId;
        this.instrumentId = instrumentId;
        this.openPrice = openPrice;
        this.summary = summary;
    }
}
