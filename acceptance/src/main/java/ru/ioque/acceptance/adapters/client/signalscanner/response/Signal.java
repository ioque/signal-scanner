package ru.ioque.acceptance.adapters.client.signalscanner.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Signal {
    String ticker;
    LocalDateTime dateTime;
    boolean isBuy;
}
