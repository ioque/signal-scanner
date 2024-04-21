package ru.ioque.investfund.application.integration.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DomainEventWrapper implements IntegrationEvent {
    UUID id;
    DomainEvent domainEvent;
    LocalDateTime dateTime;

    public static DomainEventWrapper of(UUID id, DomainEvent domainEvent, LocalDateTime dateTime) {
        return new DomainEventWrapper(id, domainEvent, dateTime);
    }
}
