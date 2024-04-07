package ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.CurrencyPair;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.IntradayValue;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("CurrencyPairEntity")
public class CurrencyPairEntity extends InstrumentEntity {
    Integer lotSize;
    String faceUnit;

    @Builder
    public CurrencyPairEntity(
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotSize,
        String faceUnit
    ) {
        super(id, ticker, shortName, name, updatable);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    public static InstrumentEntity from(CurrencyPair domain) {
        return CurrencyPairEntity.builder()
            .id(domain.getId())
            .ticker(domain.getTicker())
            .name(domain.getName())
            .shortName(domain.getShortName())
            .lotSize(domain.getLotSize())
            .faceUnit(domain.getFaceUnit())
            .updatable(domain.getUpdatable())
            .build();
    }

    @Override
    public Instrument toDomain() {
        return CurrencyPair.builder()
            .id(this.getId())
            .ticker(this.getTicker())
            .name(this.getName())
            .shortName(this.getShortName())
            .updatable(this.getUpdatable())
            .lotSize(this.getLotSize())
            .faceUnit(this.getFaceUnit())
            .build();
    }

    @Override
    public Instrument toDomain(
        List<HistoryValue> historyValues,
        List<IntradayValue> intradayValues
    ) {
        return CurrencyPair.builder()
            .id(this.getId())
            .ticker(this.getTicker())
            .name(this.getName())
            .shortName(this.getShortName())
            .lotSize(this.getLotSize())
            .faceUnit(this.getFaceUnit())
            .updatable(this.getUpdatable())
            .historyValues(historyValues)
            .intradayValues(intradayValues)
            .build();
    }
}
