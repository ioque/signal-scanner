package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Delta extends IntradayValue {
    @Builder
    public Delta(
        DatasourceId datasourceId,
        InstrumentId instrumentId,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Double value
    ) {
        super(datasourceId, instrumentId, number, dateTime, price, value);
    }
}
