package ru.ioque.investfund.domain.datasource.value.details;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.Isin;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockDetails extends InstrumentDetails {
    Integer lotSize;
    Isin isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public StockDetails(
        String shortName,
        String name,
        Integer lotSize,
        Isin isin,
        String regNumber,
        Integer listLevel
    ) {
        super(shortName, name);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }
}
