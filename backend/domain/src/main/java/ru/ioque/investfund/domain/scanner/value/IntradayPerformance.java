package ru.ioque.investfund.domain.scanner.value;

import java.time.LocalDateTime;

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
    LocalDateTime timestamp;

    public static IntradayPerformance of(IntradayData data) {
        return IntradayPerformance.builder()
            .instrumentId(data.getInstrumentId())
            .timestamp(data.getDateTime())
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
        if(timestamp != null && data.getDateTime().toLocalDate().isAfter(timestamp.toLocalDate())) {
            return IntradayPerformance.of(data);
        }
        return IntradayPerformance.builder()
            .instrumentId(data.getInstrumentId())
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
