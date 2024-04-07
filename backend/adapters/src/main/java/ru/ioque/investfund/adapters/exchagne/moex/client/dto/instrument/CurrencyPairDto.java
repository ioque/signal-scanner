package ru.ioque.investfund.adapters.exchagne.moex.client.dto.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.CurrencyPair;
import ru.ioque.investfund.domain.datasource.entity.Instrument;

import java.util.ArrayList;
import java.util.UUID;

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
    public Instrument toDomain(UUID id) {
        return CurrencyPair.builder()
            .id(id)
            .faceUnit(faceUnit)
            .lotSize(lotSize)
            .ticker(getTicker())
            .shortName(getShortName())
            .name(getName())
            .historyValues(new ArrayList<>())
            .intradayValues(new ArrayList<>())
            .build();
    }
}
