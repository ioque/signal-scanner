package ru.ioque.investfund.domain.datasource.value.details;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyPairDetails extends InstrumentDetails {
    InstrumentType type = InstrumentType.CURRENCY_PAIR;
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPairDetails(
        Ticker ticker,
        String shortName,
        String name,
        Integer lotSize,
        String faceUnit
    ) {
        super(ticker, shortName, name);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }
}
