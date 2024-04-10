package ru.ioque.investfund.domain.core;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@ToString
@SuperBuilder
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Domain implements Serializable {
    UUID id;

    public Domain(UUID id) {
        setId(id);
    }

    private void setId(UUID id) {
        if (id == null) throw new DomainException("Не передан идентификатор объекта.");
        this.id = id;
    }
}
