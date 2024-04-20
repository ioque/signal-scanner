package ru.ioque.investfund.domain.datasource.value.details;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@ToString
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class InstrumentDetails {
    @Valid
    Ticker ticker;
    @NotBlank(message = "не заполнено краткое наименование инструмента.")
    String shortName;
    @NotBlank(message = "не заполнено полное наименование инструмента.")
    String name;

    public abstract InstrumentType getType();
}
