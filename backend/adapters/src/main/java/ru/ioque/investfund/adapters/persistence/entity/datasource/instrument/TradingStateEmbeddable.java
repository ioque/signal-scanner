package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.TradingState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradingStateEmbeddable {
    LocalDate date;
    LocalTime time;
    Double todayValue;
    Double todayLastPrice;
    Double todayFirstPrice;
    Long lastIntradayNumber;

    public LocalDateTime getDateTime() {
        return date.atTime(time);
    }

    public static TradingStateEmbeddable from(TradingState tradingState) {
        return new TradingStateEmbeddable(
            tradingState.getDate(),
            tradingState.getTime(),
            tradingState.getTodayValue(),
            tradingState.getTodayLastPrice(),
            tradingState.getTodayFirstPrice(),
            tradingState.getLastIntradayNumber()
        );
    }

    public TradingState toTradingState() {
        return new TradingState(
            getDate(),
            getTime(),
            getTodayValue(),
            getTodayLastPrice(),
            getTodayFirstPrice(),
            getLastIntradayNumber()
        );
    }
}
