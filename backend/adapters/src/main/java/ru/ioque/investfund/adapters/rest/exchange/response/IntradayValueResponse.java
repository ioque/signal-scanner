package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.archive.intradayvalue.ArchivedIntradayValueEntity;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

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

    public static IntradayValueResponse fromDomain(IntradayValue intradayValue) {
        return IntradayValueResponse.builder()
            .number(intradayValue.getNumber())
            .dateTime(intradayValue.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .price(intradayValue.getPrice())
            .build();
    }

    public static IntradayValueResponse fromEntity(ArchivedIntradayValueEntity intradayValueEntity) {
        return IntradayValueResponse.builder()
            .number(intradayValueEntity.getNumber())
            .dateTime(intradayValueEntity.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .price(intradayValueEntity.getPrice())
            .build();
    }
}
