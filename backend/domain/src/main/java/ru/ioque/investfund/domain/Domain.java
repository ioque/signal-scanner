package ru.ioque.investfund.domain;

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
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class Domain implements Serializable {
    UUID id;

    public Domain(UUID id) {
        this.id = id;
        if (this.id == null) throw new DomainException("Не передан идентификатор объекта.");
    }
}
