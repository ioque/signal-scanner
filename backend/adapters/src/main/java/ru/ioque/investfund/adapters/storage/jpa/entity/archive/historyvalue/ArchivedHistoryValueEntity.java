package ru.ioque.investfund.adapters.storage.jpa.entity.archive.historyvalue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.exchange.value.HistoryValue;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity(name = "ArchivedHistoryValue")
@Table(name = "archived_history_value")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArchivedHistoryValueEntity {
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

    public ArchivedHistoryValueEntity(
        LocalDate tradeDate,
        String ticker,
        Double openPrice,
        Double closePrice,
        Double minPrice,
        Double maxPrice,
        Double value
    ) {
        this.tradeDate = tradeDate;
        this.ticker = ticker;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.value = value;
    }

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

    public static ArchivedHistoryValueEntity fromDomain(HistoryValue historyValue) {
        return ArchivedHistoryValueEntity.builder()
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