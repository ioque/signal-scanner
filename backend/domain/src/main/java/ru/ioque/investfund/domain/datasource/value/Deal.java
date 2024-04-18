package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;

import java.time.LocalDateTime;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Deal extends IntradayValue {
    Boolean isBuy;
    Integer qnt;

    @Builder
    public Deal(
        InstrumentId instrumentId,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Boolean isBuy,
        Integer qnt,
        Double value
    ) {
        super(instrumentId, number, dateTime, price, value);
        setBuy(isBuy);
        setQnt(qnt);
    }

    private void setBuy(Boolean buy) {
        if (buy == null) {
            throw new DomainException("Не заполнен признак покупки/продажи");
        }
        isBuy = buy;
    }

    private void setQnt(Integer qnt) {
        if (qnt == null) {
            throw new DomainException("Не заполнено количество купленных лотов.");
        }
        if (qnt <= 0) {
            throw new DomainException("Количество купленных лотов должно быть больше нуля.");
        }
        this.qnt = qnt;
    }
}
