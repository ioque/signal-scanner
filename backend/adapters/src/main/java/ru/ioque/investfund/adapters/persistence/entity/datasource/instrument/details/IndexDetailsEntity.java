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
import ru.ioque.investfund.domain.datasource.value.details.IndexDetails;
import ru.ioque.investfund.domain.datasource.value.details.InstrumentDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("Index")
public class IndexDetailsEntity extends InstrumentDetailsEntity {
    Double annualHigh;

    Double annualLow;

    @Builder
    public IndexDetailsEntity(
        InstrumentEntity instrument,
        String shortName,
        String name,
        Double annualHigh,
        Double annualLow
    ) {
        super(instrument, shortName, name);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }

    @Override
    public InstrumentDetails toDomain() {
        return IndexDetails.builder()
            .name(getName())
            .shortName(getShortName())
            .annualHigh(getAnnualHigh())
            .annualLow(getAnnualLow())
            .build();
    }
}
