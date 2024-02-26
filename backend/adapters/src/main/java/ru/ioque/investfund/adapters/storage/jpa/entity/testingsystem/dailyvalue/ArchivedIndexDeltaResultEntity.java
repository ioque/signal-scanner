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
import ru.ioque.investfund.domain.exchange.value.tradingData.IndexDeltaResult;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("IndexDeltaResultEntity")
public class ArchivedIndexDeltaResultEntity extends
    ArchivedDailyValueEntity {
    Double capitalization;

    @Builder
    public ArchivedIndexDeltaResultEntity(
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double value,
        Double capitalization
    ) {
        super(tradeDate, ticker, openPrice, closePrice, minPrice, maxPrice, value);
        this.capitalization = capitalization;
    }

    public static ArchivedDailyValueEntity from(IndexDeltaResult domain) {
        return ArchivedIndexDeltaResultEntity.builder()
            .ticker(domain.getTicker())
            .tradeDate(domain.getTradeDate())
            .openPrice(domain.getOpenPrice())
            .closePrice(domain.getClosePrice())
            .minPrice(domain.getMinPrice())
            .maxPrice(domain.getMaxPrice())
            .value(domain.getValue())
            .capitalization(domain.getCapitalization())
            .build();
    }

    @Override
    public DailyValue toDomain() {
        return IndexDeltaResult.builder()
            .ticker(ticker)
            .tradeDate(tradeDate)
            .openPrice(openPrice)
            .closePrice(closePrice)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .value(value)
            .capitalization(capitalization)
            .build();
    }
}
