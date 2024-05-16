package ru.ioque.investfund.domain.datasource.value.details;

import jakarta.validation.constraints.DecimalMin;
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
public class IndexDetail extends InstrumentDetail {
    @DecimalMin(value = "0", message = "Годовой максимум индекса должен быть положительным числом.")
    Double annualHigh;
    @DecimalMin(value = "0", message = "Годовой минимум индекса должен быть положительным числом.")
    Double annualLow;

    @Builder
    public IndexDetail(
        Ticker ticker,
        String shortName,
        String name,
        Double annualHigh,
        Double annualLow
    ) {
        super(ticker, shortName, name);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }

    @Override
    public InstrumentType getType() {
        return InstrumentType.INDEX;
    }
}
