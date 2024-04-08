package ru.ioque.investfund.adapters.exchagne.moex.client.dto.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.Index;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.UUID;

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
    public Instrument toDomain(UUID id) {
        return Index.builder()
            .id(id)
            .ticker(getTicker())
            .shortName(getShortName())
            .name(getName())
            .annualHigh(annualHigh)
            .annualLow(annualLow)
            .build();
    }
}
