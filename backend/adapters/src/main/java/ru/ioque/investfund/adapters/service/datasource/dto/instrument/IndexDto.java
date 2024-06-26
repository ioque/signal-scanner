package ru.ioque.investfund.adapters.service.datasource.dto.instrument;

import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetail;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IndexDto extends InstrumentDto {
    @DecimalMin(value = "0", message = "Годовой максимум индекса должен быть положительным числом.")
    Double annualHigh;
    @DecimalMin(value = "0", message = "Годовой минимум индекса должен быть положительным числом.")
    Double annualLow;

    @Builder
    public IndexDto(String ticker, String shortName, String name, Double annualHigh, Double annualLow) {
        super(ticker, shortName, name);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }

    @Override
    public InstrumentDetail toDetails() {
        return IndexDetail.builder()
            .ticker(Ticker.from(getTicker()))
            .shortName(getShortName())
            .name(getName())
            .annualHigh(annualHigh)
            .annualLow(annualLow)
            .build();
    }

    @Override
    public String toString() {
        return "IndexDto{" +
            "ticker=" + getTicker() +
            ", shortName=" + getShortName() +
            ", name=" + getName() +
            ", annualHigh=" + annualHigh +
            ", assetCode='" + annualLow + '\'' +
            '}';
    }
}
