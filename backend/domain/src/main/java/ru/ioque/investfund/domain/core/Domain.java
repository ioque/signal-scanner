package ru.ioque.investfund.domain.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@ToString
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Domain<Identity> {
    final Identity id;
    @ToString.Exclude
    final Set<DomainEvent> events = new HashSet<>();

    protected Domain(Identity id) {
        this.id = id;
    }

    protected void addEvent(DomainEvent event) {
        this.events.add(event);
    }

    public List<DomainEvent> getEvents() {
        return List.copyOf(events);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domain<?> domain = (Domain<?>) o;
        return Objects.equals(id, domain.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
