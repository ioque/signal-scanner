package ru.ioque.investfund.domain.core;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class DomainEvent implements Comparable<DomainEvent> {
    private Instant timestamp;

    @Override
    public int compareTo(DomainEvent event) {
        return timestamp.compareTo(event.timestamp);
    }
}
