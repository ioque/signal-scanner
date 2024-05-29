package ru.ioque.investfund.domain.scanner.value;

import java.time.LocalDateTime;

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
public class IntradayPerformance implements Comparable<IntradayPerformance> {

    Ticker ticker;
    Double todayValue;
    Double todayLastPrice;
    Double todayFirstPrice;
    LocalDateTime timestamp;

    public static IntradayPerformance empty() {
        return IntradayPerformance.builder()
            .todayValue(0D)
            .todayLastPrice(0D)
            .todayFirstPrice(0D)
            .build();
    }

    public static IntradayPerformance from(IntradayData data) {
        return IntradayPerformance.builder()
            .ticker(data.getTicker())
            .timestamp(data.getDateTime())
            .todayLastPrice(data.getPrice())
            .todayValue(data.getValue())
            .todayFirstPrice(data.getPrice())
            .build();
    }

    public IntradayPerformance add(IntradayData data) {
        final Ticker ticker = data.getTicker();
        if (this.ticker != null && !this.ticker.equals(ticker)) {
            throw new DomainException("Нельзя изменить ранее установленный тикер.");
        }
        if(timestamp != null && data.getDateTime().toLocalDate().isAfter(timestamp.toLocalDate())) {
            return IntradayPerformance.from(data);
        }
        return IntradayPerformance.builder()
            .ticker(ticker)
            .timestamp(data.getDateTime())
            .todayLastPrice(data.getPrice())
            .todayValue(todayValue + data.getValue())
            .todayFirstPrice(todayFirstPrice > 0 ? todayFirstPrice : data.getPrice())
            .build();
    }

    @Override
    public int compareTo(IntradayPerformance intradayPerformance) {
        return this.timestamp.compareTo(intradayPerformance.timestamp);
    }
}
