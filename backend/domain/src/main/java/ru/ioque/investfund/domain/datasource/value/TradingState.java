package ru.ioque.investfund.domain.datasource.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.TreeSet;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TradingState implements Comparable<TradingState> {
    Long lastNumber;
    LocalDate date;
    LocalTime time;
    Double lastPrice;
    Double openPrice;
    Double value;

    public static TradingState from(TreeSet<IntradayData> intradayData) {
        IntradayData last = intradayData.last();
        if (Objects.isNull(last)) {
            throw new IllegalArgumentException("Список внутридневных событий пуст.");
        }
        Long lastNumber = last.getNumber();
        Double lastPrice = last.getPrice();
        LocalDate date = last.getDateTime().toLocalDate();
        LocalTime time = last.getDateTime().toLocalTime();
        Double openPrice = intradayData.first().getPrice();
        Double value = intradayData.stream().mapToDouble(IntradayData::getValue).sum();

        return TradingState.builder()
            .time(time)
            .date(date)
            .value(value)
            .openPrice(openPrice)
            .lastPrice(lastPrice)
            .lastNumber(lastNumber)
            .build();
    }

    public static TradingState of(TradingState tradingState, TreeSet<IntradayData> intradayData) {
        IntradayData last = intradayData.last();
        if (Objects.isNull(last)) {
            return tradingState;
        }
        if (last.getDateTime().toLocalDate().isAfter(tradingState.getDate())) {
            return TradingState.from(intradayData);
        }
        Long lastNumber = last.getNumber();
        Double lastPrice = last.getPrice();
        LocalTime time = last.getDateTime().toLocalTime();
        Double value = intradayData.stream().mapToDouble(IntradayData::getValue).sum() + tradingState.getValue();
        return TradingState.builder()
            .time(time)
            .value(value)
            .lastPrice(lastPrice)
            .lastNumber(lastNumber)
            .date(tradingState.getDate())
            .openPrice(tradingState.getOpenPrice())
            .build();
    }

    @Override
    public int compareTo(TradingState tradingState) {
        return date.atTime(time).compareTo(tradingState.date.atTime(time));
    }
}
