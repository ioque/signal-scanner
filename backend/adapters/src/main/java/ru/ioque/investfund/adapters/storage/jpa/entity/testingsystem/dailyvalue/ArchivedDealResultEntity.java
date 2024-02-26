package ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.dailyvalue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.DealResult;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("DealResultEntity")
public class ArchivedDealResultEntity extends ArchivedDailyValueEntity {
    Double numTrades;
    Double waPrice;
    Double volume;

    @Builder
    public ArchivedDealResultEntity(
        InstrumentEntity instrument,
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double value,
        Double numTrades,
        Double waPrice,
        Double volume
    ) {
        super(tradeDate, ticker, openPrice, closePrice, minPrice, maxPrice, value);
        this.numTrades = numTrades;
        this.waPrice = waPrice;
        this.volume = volume;
    }

    public static ArchivedDailyValueEntity from(DealResult domain) {
        return ArchivedDealResultEntity.builder()
            .ticker(domain.getTicker())
            .tradeDate(domain.getTradeDate())
            .openPrice(domain.getOpenPrice())
            .closePrice(domain.getClosePrice())
            .minPrice(domain.getMinPrice())
            .maxPrice(domain.getMaxPrice())
            .value(domain.getValue())
            .numTrades(domain.getNumTrades())
            .waPrice(domain.getWaPrice())
            .volume(domain.getVolume())
            .build();
    }

    @Override
    public DailyValue toDomain() {
        return DealResult.builder()
            .ticker(ticker)
            .tradeDate(tradeDate)
            .openPrice(openPrice)
            .closePrice(closePrice)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .value(value)
            .numTrades(numTrades)
            .waPrice(waPrice)
            .volume(volume)
            .build();
    }
}
