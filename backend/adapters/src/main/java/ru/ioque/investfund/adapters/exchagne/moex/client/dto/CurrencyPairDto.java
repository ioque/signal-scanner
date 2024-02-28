package ru.ioque.investfund.adapters.exchagne.moex.client.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.entity.CurrencyPair;
import ru.ioque.investfund.domain.exchange.entity.Instrument;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
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
    public Instrument toDomain(UUID id) {
        return CurrencyPair.builder()
            .id(id)
            .faceUnit(faceUnit)
            .lotSize(lotSize)
            .ticker(ticker)
            .shortName(shortName)
            .name(name)
            .dailyValues(new ArrayList<>())
            .intradayValues(new ArrayList<>())
            .build();
    }
}
