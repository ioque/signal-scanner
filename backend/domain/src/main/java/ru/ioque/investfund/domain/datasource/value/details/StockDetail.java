package ru.ioque.investfund.domain.datasource.value.details;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.types.InstrumentType;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StockDetail extends InstrumentDetail {
    @Min(value = 1, message = "Размер лота должен быть положительным целым числом.")
    Integer lotSize;
    @Valid Isin isin;
    String regNumber;
    @Min(value = 1, message = "Уровень листинга должен быть положительным целым числом.")
    Integer listLevel;

    @Builder
    public StockDetail(
        Ticker ticker,
        String shortName,
        String name,
        Integer lotSize,
        Isin isin,
        String regNumber,
        Integer listLevel
    ) {
        super(ticker, shortName, name);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }

    @Override
    public InstrumentType getType() {
        return InstrumentType.STOCK;
    }
}
