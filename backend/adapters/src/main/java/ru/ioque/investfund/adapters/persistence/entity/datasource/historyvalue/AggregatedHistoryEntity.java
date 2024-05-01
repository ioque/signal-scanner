package ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.datasource.instrument.InstrumentEntity;
import ru.ioque.investfund.domain.datasource.value.AggregatedHistory;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity(name = "AggregatedHistory")
@Table(name = "aggregated_history")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregatedHistoryEntity {
    @EmbeddedId
    AggregatedHistoryPk id;
    @ManyToOne
    @MapsId("instrumentId")
    @JoinColumn(name = "instrument_id")
    InstrumentEntity instrument;
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
    public AggregatedHistoryEntity(
        InstrumentEntity instrument,
        LocalDate date,
        Double openPrice,
        Double closePrice,
        Double lowPrice,
        Double highPrice,
        Double waPrice,
        Double value
    ) {
        this.id = AggregatedHistoryPk.from(instrument.getId(), date);
        this.instrument = instrument;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
        this.waPrice = waPrice;
        this.value = value;
    }

    public AggregatedHistory toDomain() {
        return AggregatedHistory.builder()
            .date(getId().getDate())
            .openPrice(openPrice)
            .closePrice(closePrice)
            .lowPrice(lowPrice)
            .highPrice(highPrice)
            .value(value)
            .waPrice(waPrice)
            .build();
    }

    public static AggregatedHistoryEntity fromDomain(InstrumentEntity instrument, AggregatedHistory historyValue) {
        return AggregatedHistoryEntity.builder()
            .instrument(instrument)
            .date(historyValue.getDate())
            .openPrice(historyValue.getOpenPrice())
            .closePrice(historyValue.getClosePrice())
            .lowPrice(historyValue.getLowPrice())
            .highPrice(historyValue.getHighPrice())
            .value(historyValue.getValue())
            .waPrice(historyValue.getWaPrice())
            .build();
    }
}
