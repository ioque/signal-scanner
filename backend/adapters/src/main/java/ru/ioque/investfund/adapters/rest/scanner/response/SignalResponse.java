package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.persistence.entity.scanner.SignalEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalResponse implements Serializable {
    Double price;
    String ticker;
    Boolean isBuy;
    String summary;
    LocalDateTime dateTime;
    public static SignalResponse from(SignalEntity signalEntity, InstrumentEntity instrument) {
        return SignalResponse.builder()
            .price(signalEntity.getPrice())
            .isBuy(signalEntity.isBuy())
            .ticker(instrument.getDetails().getTicker())
            .summary(signalEntity.getSummary())
            .dateTime(signalEntity.getDateTime())
            .build();
    }
}
