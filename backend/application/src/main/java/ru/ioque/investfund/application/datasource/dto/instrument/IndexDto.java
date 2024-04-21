package ru.ioque.investfund.application.datasource.dto.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetails;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IndexDto extends InstrumentDto {
    Double annualHigh;
    Double annualLow;

    @Builder
    public IndexDto(String ticker, String shortName, String name, Double annualHigh, Double annualLow) {
        super(ticker, shortName, name);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }

    @Override
    public InstrumentDetails toDetails() {
        return IndexDetails.builder()
            .ticker(Ticker.from(getTicker()))
            .shortName(getShortName())
            .name(getName())
            .annualHigh(annualHigh)
            .annualLow(annualLow)
            .build();
    }
}
