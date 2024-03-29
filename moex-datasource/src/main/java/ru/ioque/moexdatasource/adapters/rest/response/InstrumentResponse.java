package ru.ioque.moexdatasource.adapters.rest.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class InstrumentResponse {
    String ticker;
    String shortName;
    String name;
}
