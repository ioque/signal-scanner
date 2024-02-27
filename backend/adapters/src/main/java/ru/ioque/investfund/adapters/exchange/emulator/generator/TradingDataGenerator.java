package ru.ioque.investfund.adapters.exchange.emulator.generator;

import ru.ioque.investfund.domain.exchange.value.tradingData.Deal;
import ru.ioque.investfund.domain.exchange.value.tradingData.DealResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TradingDataGenerator {
    public List<DealResult> generateStockDealResults(StockDealResultsGeneratorConfig config) {
        if (config.getClosePricePercentageGrowths().size() == 1 &&
            config.getOpenPricePercentageGrowths().size() == 1 &&
            config.getValuePercentageGrowths().size() == 1) {

            Double startClose = config.getStartClose();
            Double startOpen = config.getStartOpen();
            Double startValue = config.getStartValue();
            int days = config.getDays();
            double finalOpen = startOpen + config.getOpenPricePercentageGrowths().get(0).value * startOpen / 100;
            double deltaOpen = (finalOpen - startOpen) / (days - 1);

            double finalClose = startClose + config.getOpenPricePercentageGrowths().get(0).value * startClose / 100;
            double deltaClose = (finalClose - startClose) / (days - 1);

            double finalValue = startValue + config.getOpenPricePercentageGrowths().get(0).value * startValue / 100;
            double deltaValue = (finalValue - startValue) / (days - 1);

            List<DealResult> dailyResults = new ArrayList<>(
                List.of(
                    DealResult.builder()
                        .ticker(config.getTicker())
                        .tradeDate(config.getStartDate())
                        .openPrice(startOpen)
                        .closePrice(startClose)
                        .value(startValue)
                        .build()
                )
            );

            for (int i = 1; i < days; i++) {
                double open = startOpen + deltaOpen * i;
                double close = startClose + deltaClose * i;
                double value = startValue + deltaValue * i;
                double volume = (long) value / ((open + close) / 2);
                dailyResults.add(
                    DealResult.builder()
                        .ticker(config.getTicker())
                        .tradeDate(config.getStartDate().plusDays(1))
                        .openPrice(open)
                        .closePrice(close)
                        .value(value)
                        .volume(volume)
                        .build()
                );
            }

            return dailyResults;
        }

        return List.of();
    }

    public List<Deal> generateStockDeals(StockDealsGeneratorConfig config) {
        if (config.getValuePercentageGrowths().size() == 1 && config.getPricePercentageGrowths().size() == 1) {
            String ticker = config.getTicker();
            Double startValue = config.getStartValue();
            Double startPrice = config.getStartPrice();
            long numTrades = config.getNumTrades();
            LocalDate nowDate = config.getDate();
            LocalTime startTime = config.getStartTime();
            boolean isBuy = config.getPricePercentageGrowths().get(0).value >= 0;
            double finalPrice = startPrice + config.getPricePercentageGrowths().get(0).value * startPrice / 100;
            double deltaPrice = (finalPrice - startPrice) / (numTrades - 1);

            double finalValue = startValue + config.getValuePercentageGrowths().get(0).value * startValue / 100;
            double deltaValue = (finalValue - startValue) / (numTrades - 1);

            List<Deal> stockTrades = new ArrayList<>(
                List.of(
                    Deal.builder()
                        .ticker(ticker)
                        .number(1L)
                        .value(startValue)
                        .price(startPrice)
                        .isBuy(isBuy)
                        .dateTime(nowDate.atTime(startTime))
                        .build()
                )
            );

            for (int i = 2; i <= numTrades; i++) {
                stockTrades.add(
                    Deal.builder()
                        .ticker(ticker)
                        .number((long) i)
                        .value(startValue + deltaValue * i)
                        .price(startPrice + deltaPrice * i)
                        .isBuy(isBuy)
                        .dateTime(nowDate.atTime(startTime.plusSeconds(i)))
                        .build()
                );
            }

            return stockTrades;
        }
        return List.of();
    }
}
