package ru.ioque.investfund.adapters.rest.risk.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.risk.EmulatedPositionEntity;

import java.util.UUID;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmulatedPositionResponse {
    UUID scannerId;
    String ticker;
    Double openPrice;
    Double lastPrice;
    Double closePrice;
    Boolean isOpen;
    Double profit;

    public static EmulatedPositionResponse from(EmulatedPositionEntity emulatedPositionEntity) {
        return EmulatedPositionResponse.builder()
            .profit(emulatedPositionEntity.getProfit())
            .isOpen(emulatedPositionEntity.getIsOpen())
            .ticker(emulatedPositionEntity.getInstrument().getTicker())
            .scannerId(emulatedPositionEntity.getScanner().getId())
            .openPrice(emulatedPositionEntity.getOpenPrice())
            .lastPrice(emulatedPositionEntity.getLastPrice())
            .closePrice(emulatedPositionEntity.getClosePrice())
            .build();
    }
}
