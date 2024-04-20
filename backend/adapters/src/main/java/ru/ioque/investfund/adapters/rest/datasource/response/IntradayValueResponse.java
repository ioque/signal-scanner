package ru.ioque.investfund.adapters.rest.datasource.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.archive.intradayvalue.ArchivedIntradayValue;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntradayValueResponse implements Serializable {
    String dateTime;
    Double price;
    Long number;

    public static IntradayValueResponse fromDomain(IntradayValueEntity intradayValueEntity) {
        return IntradayValueResponse.builder()
            .number(intradayValueEntity.getNumber())
            .dateTime(intradayValueEntity.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .price(intradayValueEntity.getPrice())
            .build();
    }

    public static IntradayValueResponse fromEntity(ArchivedIntradayValue intradayValueEntity) {
        return IntradayValueResponse.builder()
            .number(intradayValueEntity.getNumber())
            .dateTime(intradayValueEntity.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .price(intradayValueEntity.getPrice())
            .build();
    }
}
