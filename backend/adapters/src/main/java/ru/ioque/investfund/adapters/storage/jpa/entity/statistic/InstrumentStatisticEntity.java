package ru.ioque.investfund.adapters.storage.jpa.entity.statistic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.adapters.storage.jpa.entity.AbstractEntity;
import ru.ioque.investfund.domain.statistic.InstrumentStatistic;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Table(name = "instrument_statistic")
@Entity(name = "InstrumentStatistic")
public class InstrumentStatisticEntity extends AbstractEntity {
    @Column(nullable = false)
    UUID instrumentId;
    Double todayValue;
    Double historyMedianValue;
    Double todayLastPrice;
    Double todayOpenPrice;
    Double buyToSellValuesRatio;

    @Builder
    public InstrumentStatisticEntity(
        UUID id,
        UUID instrumentId,
        Double todayValue,
        Double historyMedianValue,
        Double todayLastPrice,
        Double todayOpenPrice,
        Double buyToSellValuesRatio
    ) {
        super(id);
        this.instrumentId = instrumentId;
        this.todayValue = todayValue;
        this.historyMedianValue = historyMedianValue;
        this.todayLastPrice = todayLastPrice;
        this.todayOpenPrice = todayOpenPrice;
        this.buyToSellValuesRatio = buyToSellValuesRatio;
    }

    public InstrumentStatistic toDomain() {
        return InstrumentStatistic.builder()
            .instrumentId(instrumentId)
            .todayValue(todayValue)
            .historyMedianValue(historyMedianValue)
            .todayLastPrice(todayLastPrice)
            .todayOpenPrice(todayOpenPrice)
            .buyToSellValuesRatio(buyToSellValuesRatio)
            .build();
    }

    public static InstrumentStatisticEntity from(InstrumentStatistic instrumentStatistic) {
        return InstrumentStatisticEntity.builder()
            .id(instrumentStatistic.getInstrumentId())
            .instrumentId(instrumentStatistic.getInstrumentId())
            .todayValue(instrumentStatistic.getTodayValue())
            .historyMedianValue(instrumentStatistic.getHistoryMedianValue())
            .todayLastPrice(instrumentStatistic.getTodayLastPrice())
            .todayOpenPrice(instrumentStatistic.getTodayOpenPrice())
            .buyToSellValuesRatio(instrumentStatistic.getBuyToSellValuesRatio())
            .build();
    }
}
