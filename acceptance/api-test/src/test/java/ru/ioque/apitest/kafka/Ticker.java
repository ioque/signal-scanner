package ru.ioque.apitest.kafka;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Ticker {
    String value;

    public Ticker(String value) {
        this.value = value;
    }
}
