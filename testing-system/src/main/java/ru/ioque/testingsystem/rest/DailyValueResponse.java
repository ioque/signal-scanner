package ru.ioque.testingsystem.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.testingsystem.entity.dailyvalue.DailyValueEntity;
import ru.ioque.testingsystem.entity.dailyvalue.DealResultEntity;
import ru.ioque.testingsystem.entity.dailyvalue.FuturesDealResultEntity;
import ru.ioque.testingsystem.entity.dailyvalue.IndexDeltaResultEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DailyValueResponse {
    LocalDate tradeDate;
    String ticker;
    Double openPrice;
    Double closePrice;
    Double minPrice;
    Double maxPrice;
    Double value;
    Double numTrades;
    Double waPrice;
    Double volume;
    Double openPositionValue;
    Double capitalization;

    public static DailyValueResponse from(DailyValueEntity dailyValueEntity) {
        return mappers.get(dailyValueEntity.getClass()).apply(dailyValueEntity);
    }

    private static Map<Class<? extends DailyValueEntity>, Function<DailyValueEntity, DailyValueResponse>> mappers = Map.of(
        DealResultEntity.class, DailyValueResponse::dealResultMapper,
        FuturesDealResultEntity.class, DailyValueResponse::futuresDealResultMapper,
        IndexDeltaResultEntity.class, DailyValueResponse::indexDeltaResultMapper
    );

    private static DailyValueResponse dealResultMapper(DailyValueEntity dailyValueEntity) {
        DealResultEntity dealResult = (DealResultEntity) dailyValueEntity;
        return DailyValueResponse.builder()
            .tradeDate(dealResult.getTradeDate())
            .ticker(dealResult.getTicker())
            .openPrice(dealResult.getOpenPrice())
            .closePrice(dealResult.getClosePrice())
            .minPrice(dealResult.getMinPrice())
            .maxPrice(dealResult.getMaxPrice())
            .value(dealResult.getValue())
            .numTrades(dealResult.getNumTrades())
            .waPrice(dealResult.getWaPrice())
            .volume(dealResult.getVolume())
            .build();
    }

    private static DailyValueResponse futuresDealResultMapper(DailyValueEntity dailyValueEntity) {
        FuturesDealResultEntity futuresDealResult = (FuturesDealResultEntity) dailyValueEntity;
        return DailyValueResponse.builder()
            .tradeDate(futuresDealResult.getTradeDate())
            .ticker(futuresDealResult.getTicker())
            .openPrice(futuresDealResult.getOpenPrice())
            .closePrice(futuresDealResult.getClosePrice())
            .minPrice(futuresDealResult.getMinPrice())
            .maxPrice(futuresDealResult.getMaxPrice())
            .value(futuresDealResult.getValue())
            .openPositionValue(futuresDealResult.getOpenPositionValue())
            .volume(Double.valueOf(futuresDealResult.getVolume()))
            .build();
    }

    private static DailyValueResponse indexDeltaResultMapper(DailyValueEntity dailyValueEntity) {
        IndexDeltaResultEntity deltaResult = (IndexDeltaResultEntity) dailyValueEntity;
        return DailyValueResponse.builder()
            .tradeDate(deltaResult.getTradeDate())
            .ticker(deltaResult.getTicker())
            .openPrice(deltaResult.getOpenPrice())
            .closePrice(deltaResult.getClosePrice())
            .minPrice(deltaResult.getMinPrice())
            .maxPrice(deltaResult.getMaxPrice())
            .value(deltaResult.getValue())
            .capitalization(deltaResult.getCapitalization())
            .build();
    }
}
