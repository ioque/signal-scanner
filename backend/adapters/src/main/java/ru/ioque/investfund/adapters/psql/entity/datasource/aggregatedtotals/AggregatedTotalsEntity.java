package ru.ioque.investfund.adapters.psql.entity.datasource.aggregatedtotals;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.history.AggregatedTotals;
import ru.ioque.investfund.domain.datasource.value.types.Ticker;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity(name = "AggregatedTotals")
@Table(name = "aggregated_totals")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregatedTotalsEntity {

    @EmbeddedId
    AggregatedTotalsPk id;

    String ticker;

    @Column(nullable = false)
    Double openPrice;

    @Column(nullable = false)
    Double closePrice;

    Double lowPrice;

    Double highPrice;

    Double waPrice;

    @Column(nullable = false)
    Double value;

    @Builder
    public AggregatedTotalsEntity(
        LocalDate date,
        UUID instrumentId,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double lowPrice,
        Double highPrice,
        Double waPrice,
        Double value
    ) {
        this.id = AggregatedTotalsPk.from(instrumentId, date);
        this.ticker = ticker;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
        this.waPrice = waPrice;
        this.value = value;
    }

    public AggregatedTotals toDomain() {
        return AggregatedTotals.builder()
            .date(getId().getDate())
            .instrumentId(InstrumentId.from(id.getInstrumentId()))
            .ticker(Ticker.from(ticker))
            .openPrice(openPrice)
            .closePrice(closePrice)
            .lowPrice(lowPrice)
            .highPrice(highPrice)
            .value(value)
            .waPrice(waPrice)
            .build();
    }

    public static AggregatedTotalsEntity from(AggregatedTotals aggregatedTotals) {
        return AggregatedTotalsEntity.builder()
            .date(aggregatedTotals.getDate())
            .instrumentId(aggregatedTotals.getInstrumentId().getUuid())
            .ticker(aggregatedTotals.getTicker().getValue())
            .openPrice(aggregatedTotals.getOpenPrice())
            .closePrice(aggregatedTotals.getClosePrice())
            .lowPrice(aggregatedTotals.getLowPrice())
            .highPrice(aggregatedTotals.getHighPrice())
            .value(aggregatedTotals.getValue())
            .waPrice(aggregatedTotals.getWaPrice())
            .build();
    }
}
