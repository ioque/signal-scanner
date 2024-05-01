package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.details;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;
import ru.ioque.investfund.domain.datasource.value.details.StockDetails;
import ru.ioque.investfund.domain.datasource.value.types.Isin;

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
        String shortName,
        String name,
        Integer lotSize,
        String isin,
        String regNumber,
        Integer listLevel
    ) {
        super(instrument, shortName, name);
        this.lotSize = lotSize;
        this.isin = isin;
        this.regNumber = regNumber;
        this.listLevel = listLevel;
    }

    @Override
    public InstrumentDetails toDomain() {
        return StockDetails.builder()
            .name(getName())
            .shortName(getShortName())
            .lotSize(getLotSize())
            .isin(Isin.from(getIsin()))
            .regNumber(getRegNumber())
            .listLevel(getListLevel())
            .build();
    }
}
