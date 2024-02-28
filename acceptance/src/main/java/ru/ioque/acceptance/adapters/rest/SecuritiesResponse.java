package ru.ioque.acceptance.adapters.rest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.ioque.acceptance.domain.dataemulator.core.InstrumentValue;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SecuritiesResponse {
    ObjectsWrapper securities;

    public static SecuritiesResponse fromBy(List<? extends InstrumentValue> instrumentValues) {
        return new SecuritiesResponse(ObjectsWrapper.fromDataset(instrumentValues));
    }
}
