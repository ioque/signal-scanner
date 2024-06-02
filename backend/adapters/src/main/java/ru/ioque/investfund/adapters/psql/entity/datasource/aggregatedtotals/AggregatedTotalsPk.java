package ru.ioque.investfund.adapters.psql.entity.datasource.aggregatedtotals;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@ToString
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedTotalsPk {
    private LocalDate date;
    private UUID instrumentId;

    public static AggregatedTotalsPk from(UUID instrumentId, LocalDate date) {
        return new AggregatedTotalsPk(date, instrumentId);
    }
}
