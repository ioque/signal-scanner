package ru.ioque.investfund.domain.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Domain<Identity> {
    final Identity id;
    final List<DomainEvent> events = new ArrayList<>();

    protected Domain(Identity id) {
        this.id = id;
    }

    public void addEvent(DomainEvent event) {
        events.add(event);
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
