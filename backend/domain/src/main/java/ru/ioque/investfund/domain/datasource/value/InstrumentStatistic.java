package ru.ioque.investfund.domain.datasource.value;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.DomainException;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstrumentStatistic {
    Ticker ticker;
    Double todayValue;
    Double todayLastPrice;
    Double todayFirstPrice;

    public static InstrumentStatistic empty() {
        return InstrumentStatistic.builder()
                .todayValue(0D)
                .todayLastPrice(0D)
                .todayFirstPrice(0D)
                .build();
    }

    public InstrumentStatistic add(String key, IntradayData data) {
        final Ticker ticker = Ticker.from(key);
        if (this.ticker != null && !this.ticker.equals(ticker)) {
            throw new DomainException("Нельзя изменить ранее установленный тикер.");
        }
        return InstrumentStatistic.builder()
                .ticker(ticker)
                .todayValue(todayValue + data.getValue())
                .todayLastPrice(data.getPrice())
                .todayFirstPrice(todayFirstPrice > 0 ? todayFirstPrice : data.getPrice())
                .build();
    }
}
