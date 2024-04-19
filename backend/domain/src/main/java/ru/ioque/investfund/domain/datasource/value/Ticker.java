package ru.ioque.investfund.domain.datasource.value;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.regex.Pattern;

@ToString
@EqualsAndHashCode
public class Ticker {
    String value;

    public Ticker(String value) {
        this.value = value;
        if (!Pattern.matches("^[A-Za-z0-9]+$",value)) {
            throw new IllegalArgumentException("Ticker must be contains latin charters or number.");
        }
    }

    public static Ticker from(String value) {
        return new Ticker(value);
    }
}
