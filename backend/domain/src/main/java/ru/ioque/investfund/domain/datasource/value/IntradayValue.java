package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
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
        setDatasourceId(datasourceId);
        setNumber(number);
        setDateTime(dateTime);
        setTicker(ticker);
        setPrice(price);
        setValue(value);
    }

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return Objects.compare(getNumber(), intradayValue.getNumber(), Long::compareTo);
    }

    private void setDatasourceId(UUID datasourceId) {
        if(datasourceId == null) {
            throw new DomainException("Не заполнен идентификатор источника данных.");
        }
        this.datasourceId = datasourceId;
    }

    private void setNumber(Long number) {
        if(number == null) {
            throw new DomainException("Не заполнен номер сделки/контракта/дельты.");
        }
        if(number <= 0) {
            throw new DomainException("Номер сделки/контракта/дельты должен быть целым положительным числом.");
        }
        this.number = number;
    }

    private void setDateTime(LocalDateTime dateTime) {
        if(dateTime == null) {
            throw new DomainException("Не заполнен время совершения сделки/заключение контракта/фиксации дельты.");
        }
        this.dateTime = dateTime;
    }

    private void setTicker(String ticker) {
        if(ticker == null || ticker.isEmpty()) {
            throw new DomainException("Не заполнен тикер.");
        }
        this.ticker = ticker;
    }

    private void setPrice(Double price) {
        if(price == null) {
            throw new DomainException("Не заполнена цена.");
        }
        if(price <= 0) {
            throw new DomainException("Цена должна быть больше 0.");
        }
        this.price = price;
    }

    private void setValue(Double value) {
        if(value == null) {
            throw new DomainException("Не заполнен объем.");
        }
        if(value <= 0) {
            throw new DomainException("Объем должен быть больше 0.");
        }
        this.value = value;
    }
}
