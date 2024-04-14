package ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.domain.datasource.value.HistoryValue;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity(name = "HistoryValue")
@Table(name = "history_value", uniqueConstraints = { @UniqueConstraint(columnNames = { "datasource_id", "ticker", "trade_date" }) })
public class HistoryValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
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
            .datasourceId(datasourceId)
            .ticker(ticker)
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
            .datasourceId(historyValue.getDatasourceId())
            .ticker(historyValue.getTicker())
            .tradeDate(historyValue.getTradeDate())
            .openPrice(historyValue.getOpenPrice())
            .closePrice(historyValue.getClosePrice())
            .lowPrice(historyValue.getLowPrice())
            .highPrice(historyValue.getHighPrice())
            .value(historyValue.getValue())
            .waPrice(historyValue.getWaPrice())
            .build();
    }
}