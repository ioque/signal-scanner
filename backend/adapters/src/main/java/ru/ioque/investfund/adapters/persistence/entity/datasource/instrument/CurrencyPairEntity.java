package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDate;

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
        Long id,
        DatasourceEntity datasource,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotSize,
        String faceUnit,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id, datasource, ticker, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    @Override
    public Instrument toDomain() {
        return CurrencyPair.builder()
            .id(InstrumentId.from(Ticker.from(this.getTicker())))
            .name(this.getName())
            .shortName(this.getShortName())
            .updatable(this.getUpdatable())
            .lotSize(this.getLotSize())
            .faceUnit(this.getFaceUnit())
            .lastHistoryDate(this.getLastHistoryDate())
            .lastTradingNumber(this.getLastTradingNumber())
            .build();
    }

    public static InstrumentEntity from(CurrencyPair domain) {
        return CurrencyPairEntity.builder()
            .ticker(domain.getId().getTicker().getValue())
            .name(domain.getName())
            .shortName(domain.getShortName())
            .lotSize(domain.getLotSize())
            .faceUnit(domain.getFaceUnit())
            .updatable(domain.getUpdatable())
            .lastHistoryDate(domain.getLastHistoryDate().orElse(null))
            .lastTradingNumber(domain.getLastTradingNumber())
            .build();
    }
}
