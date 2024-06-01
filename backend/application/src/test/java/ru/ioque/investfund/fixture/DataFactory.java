package ru.ioque.investfund.fixture;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.intraday.Contract;
import ru.ioque.investfund.domain.datasource.value.intraday.Deal;
import ru.ioque.investfund.domain.datasource.value.intraday.Delta;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

public class DataFactory {
    public static List<AggregatedTotals> generateHistoryValues(
        String ticker,
        LocalDate start,
        LocalDate stop
    ) {
        final List<AggregatedTotals> historyValues = new ArrayList<>();
        var cursor = start;
        while (cursor.isBefore(stop) || cursor.isEqual(stop)) {
            if (!cursor.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !cursor.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                historyValues.add(DataFactory.factoryAggregatedHistory(ticker, cursor).build());
            }
            cursor = cursor.plusDays(1);
        }
        return historyValues;
    }

    public static Deal factoryDealFrom(
        String ticker,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Double value,
        Integer qnt,
        boolean isBuy
    ) {
        return Deal.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .timestamp(dateTime.toInstant(ZoneOffset.UTC))
            .value(value)
            .isBuy(isBuy)
            .qnt(qnt)
            .price(price)
            .build();
    }

    public static Contract factoryContractBy(
        String ticker,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Double value,
        Integer qnt
    ) {
        return Contract.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .timestamp(dateTime.toInstant(ZoneOffset.UTC))
            .price(price)
            .qnt(qnt)
            .value(value)
            .build();
    }

    public static Delta factoryDeltaBy(
        String ticker,
        Long number,
        LocalDateTime dateTime,
        Double price,
        Double value
    ) {
        return Delta.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .timestamp(dateTime.toInstant(ZoneOffset.UTC))
            .value(value)
            .price(price)
            .build();
    }

    public static AggregatedTotals factoryAggregatedHistory(
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double value
    ) {
        return AggregatedTotals.builder()
            .ticker(Ticker.from(ticker))
            .date(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .build();
    }

    public static AggregatedTotals factoryAggregatedHistory(
        String ticker,
        String tradeDate,
        Double openPrice,
        Double closePrice,
        Double waPrice,
        Double value
    ) {
        return AggregatedTotals.builder()
            .ticker(Ticker.from(ticker))
            .date(LocalDate.parse(tradeDate))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .highPrice(1D)
            .lowPrice(1D)
            .value(value)
            .waPrice(waPrice)
            .build();
    }

    public static AggregatedTotals.AggregatedTotalsBuilder factoryAggregatedHistory(
        String ticker,
        LocalDate localDate
    ) {
        return AggregatedTotals.builder()
            .ticker(Ticker.from(ticker))
            .date(localDate)
            .openPrice(1.0)
            .closePrice(1.0)
            .lowPrice(1.0)
            .highPrice(1.0)
            .value(1.0)
            .waPrice(1D);
    }

    public static Deal factoryDealWith(String ticker, Long number, LocalDateTime dateTime) {
        return Deal.builder()
            .ticker(Ticker.from(ticker))
            .number(number)
            .timestamp(dateTime.toInstant(ZoneOffset.UTC))
            .value(10000.0)
            .isBuy(false)
            .qnt(1)
            .price(100D)
            .build();
    }
}
