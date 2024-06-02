package ru.ioque.investfund.adapters.rest.archive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.psql.entity.datasource.intradayvalue.IntradayDataEntity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntradayDtoResponse implements Serializable {
    Long number;
    Double price;
    String ticker;
    Instant dateTime;

    public static IntradayDtoResponse from(IntradayDataEntity intradayDataEntity) {
        return IntradayDtoResponse.builder()
            .price(intradayDataEntity.getPrice())
            .dateTime(intradayDataEntity.getTimestamp())
            .ticker(intradayDataEntity.getId().getTicker())
            .number(intradayDataEntity.getId().getNumber())
            .build();
    }
}
