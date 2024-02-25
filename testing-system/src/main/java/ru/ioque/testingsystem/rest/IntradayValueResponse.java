package ru.ioque.testingsystem.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.testingsystem.entity.intradayvalue.DealEntity;
import ru.ioque.testingsystem.entity.intradayvalue.FuturesDealEntity;
import ru.ioque.testingsystem.entity.intradayvalue.IndexDeltaEntity;
import ru.ioque.testingsystem.entity.intradayvalue.IntradayValueEntity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntradayValueResponse {
    LocalDateTime dateTime;
    Long number;
    String ticker;
    Double price;
    Double value;
    Integer qnt;
    Boolean isBuy;

    public static IntradayValueResponse from(IntradayValueEntity intradayValueEntity) {
        return mappers.get(intradayValueEntity.getClass()).apply(intradayValueEntity);
    }

    private static Map<Class<? extends IntradayValueEntity>, Function<IntradayValueEntity, IntradayValueResponse>> mappers = Map.of(
        DealEntity.class, IntradayValueResponse::dealResultMapper,
        FuturesDealEntity.class, IntradayValueResponse::futuresDealResultMapper,
        IndexDeltaEntity.class, IntradayValueResponse::indexDeltaResultMapper
    );

    private static IntradayValueResponse dealResultMapper(IntradayValueEntity intradayValueEntity) {
        DealEntity deal = (DealEntity) intradayValueEntity;
        return IntradayValueResponse.builder()
            .dateTime(deal.getDateTime())
            .number(deal.getNumber())
            .ticker(deal.getTicker())
            .price(deal.getPrice())
            .value(deal.getValue())
            .qnt(deal.getQnt())
            .isBuy(deal.getIsBuy())
            .build();
    }

    private static IntradayValueResponse futuresDealResultMapper(IntradayValueEntity intradayValueEntity) {
        FuturesDealEntity deal = (FuturesDealEntity) intradayValueEntity;
        return IntradayValueResponse.builder()
            .dateTime(deal.getDateTime())
            .number(deal.getNumber())
            .ticker(deal.getTicker())
            .price(deal.getPrice())
            .qnt(deal.getQnt())
            .build();
    }

    private static IntradayValueResponse indexDeltaResultMapper(IntradayValueEntity intradayValueEntity) {
        IndexDeltaEntity delta = (IndexDeltaEntity) intradayValueEntity;
        return IntradayValueResponse.builder()
            .dateTime(delta.getDateTime())
            .number(delta.getNumber())
            .ticker(delta.getTicker())
            .price(delta.getPrice())
            .value(delta.getValue())
            .build();
    }
}
