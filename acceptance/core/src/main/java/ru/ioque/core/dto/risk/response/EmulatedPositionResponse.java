package ru.ioque.core.dto.risk.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Builder
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmulatedPositionResponse {
    UUID scannerId;
    String ticker;
    Double openPrice;
    Double lastPrice;
    Double closePrice;
    Boolean isOpen;
    Double profit;
}
