package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

import java.time.LocalDateTime;

@Getter(AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract extends IntradayValue {
    Integer qnt;

    @Builder
    public Contract(
        DatasourceId datasourceId,
        InstrumentId instrumentId,
        Long number,
        LocalDateTime dateTime,
        String ticker,
        Double price,
        Double value,
        Integer qnt
    ) {
        super(datasourceId, instrumentId, number, dateTime, price, value);
        setQnt(qnt);
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
