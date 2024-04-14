package ru.ioque.investfund.domain.scanner.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.ioque.investfund.domain.core.DomainEvent;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SignalEvent implements DomainEvent {
    LocalDateTime dateTime;
    String ticker;

    public static DomainEvent from(Signal signal) {
        return new SignalEvent(signal.getDateTime(), signal.getTicker());
    }
}
