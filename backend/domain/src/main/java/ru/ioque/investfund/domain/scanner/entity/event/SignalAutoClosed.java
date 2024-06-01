package ru.ioque.investfund.domain.scanner.entity.event;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.identifier.SignalId;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalAutoClosed extends SignalEvent {

    Double closePrice;

    @Builder
    public SignalAutoClosed(SignalId signalId, Instant timestamp, Double closePrice) {
        super(signalId, timestamp);
        this.closePrice = closePrice;
    }
}
