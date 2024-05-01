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
public class IndexDetails extends InstrumentDetails {
    Double annualHigh;
    Double annualLow;

    @Builder
    public IndexDetails(
        String shortName,
        String name,
        Double annualHigh,
        Double annualLow
    ) {
        super(shortName, name);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }
}
