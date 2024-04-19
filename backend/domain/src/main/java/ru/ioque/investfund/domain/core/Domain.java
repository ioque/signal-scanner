package ru.ioque.investfund.domain.core;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Domain {
    final Set<DomainEvent> events = new HashSet<>();

    public void addEvent(DomainEvent event) {
        this.events.add(event);
    }
}
