package ru.ioque.investfund.domain.datasource.entity.indetity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InstrumentId {
    String ticker;
    DatasourceId datasourceId;

    public static InstrumentId of(String ticker, DatasourceId datasourceId) {
        return new InstrumentId(ticker, datasourceId);
    }
}
