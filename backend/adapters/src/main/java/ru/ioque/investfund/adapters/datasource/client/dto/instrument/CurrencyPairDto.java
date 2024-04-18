package ru.ioque.investfund.adapters.datasource.client.dto.instrument;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.CurrencyPair;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.indetity.InstrumentId;

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
    public Instrument toDomain(UUID datasourceId) {
        return CurrencyPair.builder()
            .id(InstrumentId.of(getTicker(), datasourceId))
            .faceUnit(faceUnit)
            .lotSize(lotSize)
            .ticker(getTicker())
            .shortName(getShortName())
            .name(getName())
            .build();
    }
}
