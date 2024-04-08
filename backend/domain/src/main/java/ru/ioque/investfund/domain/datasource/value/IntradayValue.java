package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class IntradayValue implements Serializable, Comparable<IntradayValue> {
    UUID datasourceId;
    Long number;
    LocalDateTime dateTime;
    String ticker;
    Double price;
    Double value;

    public IntradayValue(
        UUID datasourceId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value
    ) {
        this.datasourceId = datasourceId;
        this.number = number;
        this.dateTime = dateTime;
        this.ticker = ticker;
        this.price = price;
        this.value = value;
    }

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return Objects.compare(getNumber(), intradayValue.getNumber(), Long::compareTo);
    }
}
