package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.intradayvalue.ArchivedIntradayValueEntity;
import ru.ioque.investfund.domain.exchange.value.IntradayValue;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntradayValueResponse implements Serializable {
    LocalDateTime dateTime;
    Double price;
    Long tradeNumber;

    public static IntradayValueResponse fromDomain(IntradayValue deal) {
        return IntradayValueResponse.builder()
            .dateTime(deal.getDateTime())
            .price(deal.getPrice())
            .tradeNumber(deal.getNumber())
            .build();
    }

    public static IntradayValueResponse fromEntity(ArchivedIntradayValueEntity intradayValueEntity) {
        return IntradayValueResponse.builder()
            .dateTime(intradayValueEntity.getDateTime())
            .price(intradayValueEntity.getPrice())
            .tradeNumber(intradayValueEntity.getNumber())
            .build();
    }
}
