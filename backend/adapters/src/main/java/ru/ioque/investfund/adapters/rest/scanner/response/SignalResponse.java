package ru.ioque.investfund.adapters.rest.scanner.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.storage.jpa.entity.exchange.instrument.InstrumentEntity;
import ru.ioque.investfund.adapters.storage.jpa.entity.scanner.SignalEntity;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignalResponse implements Serializable {
    String ticker;
    String dateTime;
    Boolean isBuy;
    public static SignalResponse from(SignalEntity signalEntity, InstrumentEntity instrument) {
        return SignalResponse.builder()
            .ticker(instrument.getTicker())
            .isBuy(signalEntity.isBuy())
            .dateTime(signalEntity.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }
}
