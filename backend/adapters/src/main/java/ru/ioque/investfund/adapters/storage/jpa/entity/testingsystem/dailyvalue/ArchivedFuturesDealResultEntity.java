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
import ru.ioque.investfund.domain.exchange.value.tradingData.DailyValue;
import ru.ioque.investfund.domain.exchange.value.tradingData.FuturesDealResult;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("FuturesDealResultEntity")
public class ArchivedFuturesDealResultEntity extends
    ArchivedDailyValueEntity {
    Double openPositionValue;
    Integer volume;

    @Builder
    public ArchivedFuturesDealResultEntity(
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double value,
        Double openPositionValue,
        Integer volume
    ) {
        super(tradeDate, ticker, openPrice, closePrice, minPrice, maxPrice, value);
        this.openPositionValue = openPositionValue;
        this.volume = volume;
    }

    public static ArchivedDailyValueEntity from(FuturesDealResult domain) {
        return ArchivedFuturesDealResultEntity.builder()
            .ticker(domain.getTicker())
            .tradeDate(domain.getTradeDate())
            .openPrice(domain.getOpenPrice())
            .closePrice(domain.getClosePrice())
            .minPrice(domain.getMinPrice())
            .maxPrice(domain.getMaxPrice())
            .value(domain.getValue())
            .openPositionValue(domain.getOpenPositionValue())
            .volume(domain.getVolume())
            .build();
    }

    @Override
    public DailyValue toDomain() {
        return FuturesDealResult.builder()
            .ticker(ticker)
            .tradeDate(tradeDate)
            .openPrice(openPrice)
            .closePrice(closePrice)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .value(value)
            .openPositionValue(openPositionValue)
            .volume(volume)
            .build();
    }
}
