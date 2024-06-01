package ru.ioque.investfund.domain.scanner.value;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IntradayPerformance implements Comparable<IntradayPerformance> {

    InstrumentId instrumentId;
    @Builder.Default
    Double todayValue = 0D;
    @Builder.Default
    Double todayLastPrice = 0D;
    @Builder.Default
    Double todayFirstPrice = 0D;
    Instant timestamp;

    public static IntradayPerformance of(IntradayData data) {
        return IntradayPerformance.builder()
            .instrumentId(data.getInstrumentId())
            .timestamp(data.getTimestamp())
            .todayLastPrice(data.getPrice())
            .todayValue(data.getValue())
            .todayFirstPrice(data.getPrice())
            .build();
    }

    public static IntradayPerformance empty(InstrumentId instrumentId) {
        return IntradayPerformance.builder()
            .instrumentId(instrumentId)
            .build();
    }

    public IntradayPerformance add(IntradayData data) {
        return IntradayPerformance.builder()
            .instrumentId(data.getInstrumentId())
            .timestamp(data.getTimestamp())
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
