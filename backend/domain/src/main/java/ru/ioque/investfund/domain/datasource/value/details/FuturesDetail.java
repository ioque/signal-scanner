package ru.ioque.investfund.domain.datasource.value.details;

import jakarta.validation.constraints.DecimalMin;
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
public class FuturesDetail extends InstrumentDetail {
    @Min(value = 0, message = "Размер лота должен быть положительным целым числом.")
    Integer lotVolume;
    @DecimalMin(value = "0", message = "Стартовая маржа фьючерса должна быть положительным числом.")
    Double initialMargin;
    @DecimalMin(value = "0", message = "Верхний лимит фьючерса должен быть положительным числом.")
    Double highLimit;
    @DecimalMin(value = "0", message = "Нижний лимит фьючерса должен быть положительным числом.")
    Double lowLimit;
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Код товара должен быть строкой, состоящей из латинских букв или цифр.")
    String assetCode;

    @Builder
    public FuturesDetail(
        Ticker ticker,
        String shortName,
        String name,
        Integer lotVolume,
        Double initialMargin,
        Double highLimit,
        Double lowLimit,
        String assetCode
    ) {
        super(ticker, shortName, name);
        this.lotVolume = lotVolume;
        this.initialMargin = initialMargin;
        this.highLimit = highLimit;
        this.lowLimit = lowLimit;
        this.assetCode = assetCode;
    }

    @Override
    public InstrumentType getType() {
        return InstrumentType.FUTURES;
    }
}
