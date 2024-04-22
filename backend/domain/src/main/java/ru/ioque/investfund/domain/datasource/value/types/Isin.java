package ru.ioque.investfund.domain.datasource.value.types;

import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Isin {
    @Pattern(regexp = "\\b([A-Z]{2})((?![A-Z]{10}\\b)[A-Z0-9]{10})\\b", message = "Неккоретное значение ISIN.")
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
