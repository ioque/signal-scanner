package ru.ioque.investfund.domain.scanner.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Signal {
    LocalDateTime dateTime;
//    Double price;
    String ticker;
    boolean isBuy;
//    String summary;

    public boolean sameByTicker(Signal signal) {
        return signal.getTicker().equals(ticker);
    }

}
