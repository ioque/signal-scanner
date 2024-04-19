package ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.adapters.persistence.entity.GeneratedIdEntity;
import ru.ioque.investfund.domain.datasource.entity.identity.DatasourceId;
import ru.ioque.investfund.domain.datasource.entity.identity.InstrumentId;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;
import ru.ioque.investfund.domain.datasource.value.Ticker;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity(name = "HistoryValue")
@Table(name = "history_value", uniqueConstraints = { @UniqueConstraint(columnNames = { "datasource_id", "ticker", "trade_date" }) })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryValueEntity extends GeneratedIdEntity {
    @Column(nullable = false)
    UUID datasourceId;
    @Column(nullable = false)
    LocalDate tradeDate;
    @Column(nullable = false)
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

    public HistoryValue toDomain() {
        return HistoryValue.builder()
            .datasourceId(DatasourceId.from(datasourceId))
            .instrumentId(InstrumentId.from(Ticker.from(ticker)))
            .tradeDate(tradeDate)
            .openPrice(openPrice)
            .closePrice(closePrice)
            .lowPrice(lowPrice)
            .highPrice(highPrice)
            .value(value)
            .waPrice(waPrice)
            .build();
    }

    public static HistoryValueEntity fromDomain(HistoryValue historyValue) {
        return HistoryValueEntity.builder()
            .datasourceId(historyValue.getDatasourceId().getUuid())
            .ticker(historyValue.getInstrumentId().getTicker().getValue())
            .tradeDate(historyValue.getTradeDate())
            .openPrice(historyValue.getOpenPrice())
            .closePrice(historyValue.getClosePrice())
            .lowPrice(historyValue.getLowPrice())
            .highPrice(historyValue.getHighPrice())
            .value(historyValue.getValue())
            .waPrice(historyValue.getWaPrice())
            .build();
    }

    @Builder
    public HistoryValueEntity(
        Long id,
        UUID datasourceId,
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double lowPrice,
        Double highPrice,
        Double waPrice,
        Double value
    ) {
        super(id);
        this.datasourceId = datasourceId;
        this.tradeDate = tradeDate;
        this.ticker = ticker;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
        this.waPrice = waPrice;
        this.value = value;
    }
}
