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
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntradayDtoResponse implements Serializable {
    String dateTime;
    Double price;
    Long number;

    public static IntradayDtoResponse from(IntradayValueEntity intradayValueEntity) {
        return IntradayDtoResponse.builder()
            .number(intradayValueEntity.getId().getNumber())
            .dateTime(intradayValueEntity.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .price(intradayValueEntity.getPrice())
            .build();
    }
}
