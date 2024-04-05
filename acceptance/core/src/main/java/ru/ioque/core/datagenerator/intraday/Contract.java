package ru.ioque.core.datagenerator.intraday;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract extends IntradayValue {
    Integer qnt;

    @Builder
    public Contract(Long number, LocalDateTime dateTime, String ticker, Double value, Double price, Integer qnt) {
        super(number, dateTime, ticker, value, price);
        this.qnt = qnt;
    }
}
