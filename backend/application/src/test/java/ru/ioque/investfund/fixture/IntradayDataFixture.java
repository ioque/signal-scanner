package ru.ioque.investfund.fixture;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import ru.ioque.investfund.application.adapters.DateTimeProvider;
import ru.ioque.investfund.domain.datasource.value.intraday.IntradayData;

import static ru.ioque.investfund.fixture.InstrumentDetailsFixture.*;


@AllArgsConstructor
public class IntradayDataFixture {
    private final DateTimeProvider dateTimeProvider;

    public IntradayData imoexDelta(Long number, String localTime, Double price, Double value) {
        return DataFactory.factoryDeltaBy(IMOEX, number, createDateTime(localTime), price, value);
    }

    public IntradayData tgknSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(TGKN, number, createDateTime(localTime), price, value, qnt, false);
    }

    public IntradayData tgknBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(TGKN, number, createDateTime(localTime), price, value, qnt, true);
    }

    public IntradayData tgkbSellDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(TGKB, number, createDateTime(localTime), price, value, qnt, false);
    }

    public IntradayData tgkbBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(TGKB, number, createDateTime(localTime), price, value, qnt, true);
    }

    public IntradayData tatnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(TATN, number, createDateTime(localTime), price, value, qnt, true);
    }

    public IntradayData brf4Contract(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryContractBy(BRF4, number, createDateTime(localTime), price, value, qnt);
    }

    public IntradayData lkohBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(LKOH, number, createDateTime(localTime), price, value, qnt, true);
    }

    public IntradayData sibnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(SIBN, number, createDateTime(localTime), price, value, qnt, true);
    }

    public IntradayData rosnBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(ROSN, number, createDateTime(localTime), price, value, qnt, true);
    }

    public IntradayData sberBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(SBER, number, createDateTime(localTime), price, value, qnt, true);
    }


    public IntradayData sberpBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(SBERP, number, createDateTime(localTime), price, value, qnt, true);
    }

    private LocalDateTime createDateTime(String localTime) {
        return dateTimeProvider.nowDate().atTime(LocalTime.parse(localTime));
    }

    public IntradayData afksBuyDeal(Long number, String localTime, Double price, Double value, Integer qnt) {
        return DataFactory.factoryDealFrom(AFKS, number, createDateTime(localTime), price, value, qnt, true);
    }
}
