package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@ToString
@EqualsAndHashCode
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class IntradayValue implements Serializable, Comparable<IntradayValue> {
    InstrumentId instrumentId;
    Long number;
    LocalDateTime dateTime;
    Double price;
    Double value;

    public IntradayValue(
        InstrumentId instrumentId,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Double value
    ) {
        setInstrumentId(instrumentId);
        setNumber(number);
        setDateTime(dateTime);
        setPrice(price);
        setValue(value);
    }

    @Override
    public int compareTo(IntradayValue intradayValue) {
        return Objects.compare(getNumber(), intradayValue.getNumber(), Long::compareTo);
    }

    private void setInstrumentId(InstrumentId instrumentId) {
        if(instrumentId == null) {
            throw new DomainException("Не заполнен идентификатор инструмента.");
        }
        this.instrumentId = instrumentId;
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
