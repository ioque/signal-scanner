package ru.ioque.investfund.adapters.rest.archive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;

import java.io.Serializable;
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
    LocalDateTime dateTime;

    public static IntradayDtoResponse from(IntradayValueEntity intradayValueEntity) {
        return IntradayDtoResponse.builder()
            .price(intradayValueEntity.getPrice())
            .dateTime(intradayValueEntity.getDateTime())
            .ticker(intradayValueEntity.getId().getTicker())
            .number(intradayValueEntity.getId().getNumber())
            .build();
    }
}
