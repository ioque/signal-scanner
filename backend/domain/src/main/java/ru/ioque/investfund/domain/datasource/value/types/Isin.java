package ru.ioque.investfund.domain.datasource.value.types;

import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Isin {
    String value;

    public Isin(String value) {
        this.value = value;
    }

    public static Isin from(String value) {
        return new Isin(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
