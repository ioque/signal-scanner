package ru.ioque.core.model.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Contract extends IntradayValue {
    Integer qnt;

    @Builder
    public Contract(Long tradeNumber, LocalDateTime dateTime, String ticker, Double value, Double price, Integer qnt) {
        super(tradeNumber, dateTime, ticker, value, price);
        this.qnt = qnt;
    }
}
