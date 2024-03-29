package ru.ioque.apitest.rest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.core.dataemulator.core.IntradayValue;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class TradesResponse {
    ObjectsWrapper trades;

    public static TradesResponse fromBy(List<? extends IntradayValue> intradayValues) {
        return new TradesResponse(ObjectsWrapper.fromDataset(intradayValues));
    }
}
