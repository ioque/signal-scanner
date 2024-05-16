package ru.ioque.investfund.domain.datasource.value.details;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyPairDetail extends InstrumentDetail {
    @Min(value = 1, message = "Размер лота должен быть положительным целым числом.")
    Integer lotSize;
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Валюта номинала должна быть строкой, состоящей из латинских букв или цифр.")
    String faceUnit;

    @Builder
    public CurrencyPairDetail(
        Ticker ticker,
        String shortName,
        String name,
        Integer lotSize,
        String faceUnit
    ) {
        super(ticker, shortName, name);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    @Override
    public InstrumentType getType() {
        return InstrumentType.CURRENCY_PAIR;
    }
}
