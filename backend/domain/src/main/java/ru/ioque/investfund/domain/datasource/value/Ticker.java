package ru.ioque.investfund.domain.datasource.value;

import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Ticker {
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Идентификатор должен быть непустой строкой, состоящей из латинских букв или цифр.")
    String value;

    public Ticker(String value) {
        this.value = value;
    }

    public static Ticker from(String value) {
        return new Ticker(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
