package ru.ioque.investfund.domain.datasource.value.types;

import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Ticker {
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Тикер должен быть непустой строкой, состоящей из латинских букв или цифр.")
    private String value;

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
