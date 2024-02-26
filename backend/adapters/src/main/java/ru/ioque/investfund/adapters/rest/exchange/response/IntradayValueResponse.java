package ru.ioque.investfund.adapters.rest.exchange.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.testingsystem.intradayvalue.ArchivedIntradayValueEntity;
import ru.ioque.investfund.domain.exchange.value.tradingData.IntradayValue;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class IntradayValueResponse implements Serializable {
    LocalDate date;
    LocalTime time;
    Double price;

    public static IntradayValueResponse fromDomain(IntradayValue deal) {
        return IntradayValueResponse.builder()
            .date(deal.getDateTime().toLocalDate())
            .time(deal.getDateTime().toLocalTime())
            .price(deal.getPrice())
            .build();
    }

    public static IntradayValueResponse fromEntity(ArchivedIntradayValueEntity intradayValueEntity) {
        return IntradayValueResponse.builder()
            .date(intradayValueEntity.getDateTime().toLocalDate())
            .time(intradayValueEntity.getDateTime().toLocalTime())
            .price(intradayValueEntity.getPrice())
            .build();
    }
}
