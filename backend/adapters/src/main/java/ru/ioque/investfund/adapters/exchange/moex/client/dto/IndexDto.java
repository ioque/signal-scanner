package ru.ioque.investfund.adapters.exchange.moex.client.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
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
            .ticker(ticker)
            .shortName(shortName)
            .name(name)
            .annualHigh(annualHigh)
            .annualLow(annualLow)
            .dailyValues(new ArrayList<>())
            .intradayValues(new ArrayList<>())
            .build();
    }
}
