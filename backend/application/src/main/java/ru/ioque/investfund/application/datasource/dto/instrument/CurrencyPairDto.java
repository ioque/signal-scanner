package ru.ioque.investfund.application.datasource.dto.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetails;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyPairDto extends InstrumentDto {
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPairDto(String ticker, String shortName, String name, Integer lotSize, String faceUnit) {
        super(ticker, shortName, name);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    @Override
    public InstrumentDetails toDetails() {
        return CurrencyPairDetails.builder()
            .ticker(Ticker.from(getTicker()))
            .faceUnit(faceUnit)
            .lotSize(lotSize)
            .shortName(getShortName())
            .name(getName())
            .build();
    }
}
