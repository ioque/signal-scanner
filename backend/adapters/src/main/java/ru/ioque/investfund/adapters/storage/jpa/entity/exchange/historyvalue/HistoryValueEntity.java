package ru.ioque.investfund.adapters.storage.jpa.entity.exchange.historyvalue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity(name = "HistoryValue")
@Table(name = "history_value")
public class HistoryValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    LocalDate tradeDate;
    @Column(nullable = false)
    String ticker;
    @Column(nullable = false)
    Double openPrice;
    @Column(nullable = false)
    Double closePrice;
    @Column(nullable = false)
    Double minPrice;
    @Column(nullable = false)
    Double maxPrice;
    Double waPrice;
    @Column(nullable = false)
    Double value;

    public HistoryValue toDomain() {
        return HistoryValue.builder()
            .ticker(ticker)
            .tradeDate(tradeDate)
            .openPrice(openPrice)
            .closePrice(closePrice)
            .lowPrice(minPrice)
            .highPrice(maxPrice)
            .value(value)
            .waPrice(waPrice)
            .build();
    }

    public static HistoryValueEntity fromDomain(HistoryValue historyValue) {
        return HistoryValueEntity.builder()
            .ticker(historyValue.getTicker())
            .tradeDate(historyValue.getTradeDate())
            .openPrice(historyValue.getOpenPrice())
            .closePrice(historyValue.getClosePrice())
            .minPrice(historyValue.getLowPrice())
            .maxPrice(historyValue.getHighPrice())
            .value(historyValue.getValue())
            .waPrice(historyValue.getWaPrice())
            .build();
    }
}
