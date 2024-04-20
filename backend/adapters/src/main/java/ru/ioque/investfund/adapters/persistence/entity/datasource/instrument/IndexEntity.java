package ru.ioque.investfund.adapters.persistence.entity.datasource.instrument;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.DatasourceEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue.HistoryValueEntity;
import ru.ioque.investfund.adapters.persistence.entity.datasource.intradayvalue.IntradayValueEntity;
import ru.ioque.investfund.domain.datasource.entity.Instrument;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.details.IndexDetails;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("IndexEntity")
public class IndexEntity extends InstrumentEntity {
    Double annualHigh;
    Double annualLow;

    @Builder
    public IndexEntity(
        UUID id,
        DatasourceEntity datasource,
        String ticker,
        String shortName,
        String name,
        Boolean updatable,
        List<HistoryValueEntity> dailyTradingResults,
        List<IntradayValueEntity> intradayValueEntities,
        Double annualHigh,
        Double annualLow,
        LocalDate lastHistoryDate,
        Long lastTradingNumber
    ) {
        super(id, datasource, ticker, shortName, name, updatable, lastHistoryDate, lastTradingNumber);
        this.annualHigh = annualHigh;
        this.annualLow = annualLow;
    }

    @Override
    public Instrument toDomain() {
        return Instrument.builder()
            .id(InstrumentId.from(getId()))
            .details(
                IndexDetails.builder()
                    .ticker(Ticker.from(this.getTicker()))
                    .name(this.getName())
                    .shortName(this.getShortName())
                    .annualHigh(this.getAnnualHigh())
                    .annualLow(this.getAnnualLow())
                    .build()
            )
            .updatable(this.getUpdatable())
            .lastHistoryDate(this.getLastHistoryDate())
            .lastTradingNumber(this.getLastTradingNumber())
            .build();
    }
}
