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
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.entity.Stock;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("StockEntity")
public class StockEntity extends InstrumentEntity {
    Integer lotSize;
    String isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public StockEntity(
        UUID id,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        Integer lotSize,
        String isin,
        String regNumber,
        Integer listLevel
    ) {
        super(id, ticker, shortName, name, updatable);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }

    @Override
    public Instrument toDomain(
        List<HistoryValue> historyValues,
        List<IntradayValue> intradayValues
    ) {
        return Stock.builder()
            .id(this.getId())
            .ticker(this.getTicker())
            .name(this.getName())
            .shortName(this.getShortName())
            .updatable(this.getUpdatable())
            .lotSize(this.getLotSize())
            .isin(this.getIsin())
            .regNumber(this.getRegNumber())
            .listLevel(this.getListLevel())
            .historyValues(historyValues)
            .intradayValues(intradayValues)
            .build();
    }

    @Override
    public Instrument toDomain() {
        return Stock.builder()
            .id(this.getId())
            .ticker(this.getTicker())
            .name(this.getName())
            .shortName(this.getShortName())
            .updatable(this.getUpdatable())
            .lotSize(this.getLotSize())
            .isin(this.getIsin())
            .regNumber(this.getRegNumber())
            .listLevel(this.getListLevel())
            .build();
    }

    public static InstrumentEntity from(Stock domain) {
        return StockEntity.builder()
            .id(domain.getId())
            .ticker(domain.getTicker())
            .name(domain.getName())
            .shortName(domain.getShortName())
            .lotSize(domain.getLotSize())
            .isin(domain.getIsin())
            .updatable(domain.getUpdatable())
            .regNumber(domain.getRegNumber())
            .listLevel(domain.getListLevel())
            .build();
    }
}
