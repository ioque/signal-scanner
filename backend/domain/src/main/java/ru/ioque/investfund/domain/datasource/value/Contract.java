package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Contract extends IntradayValue {
    Integer qnt;

    @Builder
    public Contract(
        UUID datasourceId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(datasourceId, number, dateTime, ticker, price, value);
        this.qnt = qnt;
    }
}
