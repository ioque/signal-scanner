package ru.ioque.investfund.application.datasource.dto.instrument;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.details.StockDetails;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockDto extends InstrumentDto {
    Integer lotSize;
    @Pattern(regexp = "\\b([A-Z]{2})((?![A-Z]{10}\\b)[A-Z0-9]{10})\\b", message = "Неккоретное значение ISIN.")
    String isin;
    String regNumber;
    Integer listLevel;

    @Builder
    public StockDto(
        String ticker,
        String shortName,
        String name,
        Integer lotSize,
        String isin,
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
    public InstrumentDetails toDetails() {
        return StockDetails.builder()
            .ticker(Ticker.from(getTicker()))
            .shortName(getShortName())
            .name(getName())
            .lotSize(lotSize)
            .isin(Isin.from(isin))
            .regNumber(regNumber)
            .listLevel(listLevel)
            .build();
    }
}
