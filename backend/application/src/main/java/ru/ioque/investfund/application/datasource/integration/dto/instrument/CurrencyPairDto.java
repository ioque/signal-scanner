package ru.ioque.investfund.application.datasource.integration.dto.instrument;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.details.CurrencyPairDetails;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyPairDto extends InstrumentDto {
    @Min(value = 1, message = "Размер лота должен быть положительным целым числом.")
    Integer lotSize;
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Валюта номинала должна быть строкой, состоящей из латинских букв или цифр.")
    String faceUnit;

    @Builder
    public CurrencyPairDto(String ticker, String shortName, String name, Integer lotSize, String faceUnit) {
        super(ticker, shortName, name);
        this.lotSize = lotSize;
        this.faceUnit = faceUnit;
    }

    @Override
    public InstrumentDetails toDetails() {
        return CurrencyPairDetails.builder()
            .ticker(Ticker.from(getTicker()))
            .faceUnit(faceUnit)
            .lotSize(lotSize)
            .shortName(getShortName())
            .name(getName())
            .build();
    }

    @Override
    public String toString() {
        return "CurrencyPairDto{" +
            "ticker=" + getTicker() +
            ", shortName=" + getShortName() +
            ", name=" + getName() +
            ", lotSize=" + lotSize +
            ", faceUnit='" + faceUnit + '\'' +
            '}';
    }
}
