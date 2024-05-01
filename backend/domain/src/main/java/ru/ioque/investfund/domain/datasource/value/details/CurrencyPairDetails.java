package ru.ioque.investfund.domain.datasource.value.details;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyPairDetails extends InstrumentDetails {
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPairDetails(
        String shortName,
        String name,
        Integer lotSize,
        String faceUnit
    ) {
        super(shortName, name);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }
}
