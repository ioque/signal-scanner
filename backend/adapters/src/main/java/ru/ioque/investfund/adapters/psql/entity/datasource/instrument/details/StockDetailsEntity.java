package ru.ioque.investfund.adapters.psql.entity.datasource.instrument.details;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.psql.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetail;
import ru.ioque.investfund.domain.datasource.value.details.StockDetail;
import ru.ioque.investfund.domain.datasource.value.types.Isin;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("Stock")
public class StockDetailsEntity extends InstrumentDetailsEntity {

    Integer lotSize;

    String isin;

    String regNumber;

    Integer listLevel;

    @Builder
    public StockDetailsEntity(
        InstrumentEntity instrument,
        String ticker,
        String shortName,
        String name,
        Integer lotSize,
        String isin,
        String regNumber,
        Integer listLevel
    ) {
        super(instrument, ticker, shortName, name);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }

    @Override
    public InstrumentDetail toDomain() {
        return StockDetail.builder()
            .ticker(Ticker.from(getTicker()))
            .name(getName())
            .shortName(getShortName())
            .lotSize(getLotSize())
            .isin(Isin.from(getIsin()))
            .regNumber(getRegNumber())
            .listLevel(getListLevel())
            .build();
    }
}
