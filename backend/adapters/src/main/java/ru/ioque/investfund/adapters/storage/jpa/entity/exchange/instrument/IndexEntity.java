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
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.ExchangeEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.dailyvalue.DailyValueEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.domain.exchange.entity.Index;
import ru.ioque.investfund.domain.exchange.entity.Instrument;
import ru.ioque.investfund.domain.exchange.value.DailyValue;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("IndexEntity")
public class IndexEntity extends InstrumentEntity {
    Double annualHigh;
    Double annualLow;

    @Builder
    public IndexEntity(
        UUID id,
        ExchangeEntity exchange,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        List<DailyValueEntity> dailyTradingResults,
        List<IntradayValueEntity> intradayValues,
        Double annualHigh,
        Double annualLow
    ) {
        super(id, ticker, shortName, name, updatable);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }

    @Override
    public Instrument toDomain(
        List<DailyValue> dailyValues,
        List<IntradayValue> intradayValues
    ) {
        return Index.builder()
            .id(this.getId())
            .ticker(this.getTicker())
            .name(this.getName())
            .shortName(this.getShortName())
            .updatable(this.getUpdatable())
            .annualHigh(this.getAnnualHigh())
            .annualLow(this.getAnnualLow())
            .dailyValues(dailyValues)
            .intradayValues(intradayValues)
            .build();
    }

    @Override
    public Instrument toDomain() {
        return Index.builder()
            .id(this.getId())
            .ticker(this.getTicker())
            .name(this.getName())
            .shortName(this.getShortName())
            .updatable(this.getUpdatable())
            .annualHigh(this.getAnnualHigh())
            .annualLow(this.getAnnualLow())
            .build();
    }

    public static InstrumentEntity from(Index domain) {
        return IndexEntity.builder()
            .id(domain.getId())
            .ticker(domain.getTicker())
            .name(domain.getName())
            .shortName(domain.getShortName())
            .updatable(domain.getUpdatable())
            .annualHigh(domain.getAnnualHigh())
            .annualLow(domain.getAnnualLow())
            .build();
    }
}
