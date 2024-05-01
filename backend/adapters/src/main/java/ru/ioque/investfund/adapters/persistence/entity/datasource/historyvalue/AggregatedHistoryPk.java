package ru.ioque.investfund.adapters.persistence.entity.datasource.historyvalue;

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
public class AggregatedHistoryPk {
    private LocalDate date;
    private UUID instrumentId;

    public static AggregatedHistoryPk from(UUID instrumentId, LocalDate date) {
        return new AggregatedHistoryPk(date, instrumentId);
    }
}
