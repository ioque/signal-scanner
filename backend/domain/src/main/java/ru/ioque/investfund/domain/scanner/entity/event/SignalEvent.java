package ru.ioque.investfund.domain.scanner.entity.event;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainEvent;
import ru.ioque.investfund.domain.scanner.entity.identifier.SignalId;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SignalEvent extends DomainEvent {
    private SignalId signalId;

    public SignalEvent(SignalId signalId, Instant timestamp) {
        super(timestamp);
        this.signalId = signalId;
    }
}
