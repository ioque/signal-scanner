package ru.ioque.investfund.domain.datasource.value.details;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public abstract class InstrumentDetail {
    @NotNull(message = "Не заполнен тикер инструмента.")
    @Valid Ticker ticker;

    @NotBlank(message = "Не заполнено краткое наименование инструмента.")
    String shortName;

    @NotBlank(message = "Не заполнено полное наименование инструмента.")
    String name;

    public abstract InstrumentType getType();
}
