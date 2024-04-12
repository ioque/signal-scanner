package ru.ioque.investfund.domain.scanner.value;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.scanner.entity.ScannerLog;
import ru.ioque.investfund.domain.scanner.entity.Signal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScanningResult {
    LocalDateTime dateTime;
    List<TickerSummary> tickerSummaries;
    List<Signal> signals;

    public ScannerLog toScannerLog() {
        StringBuilder summary = new StringBuilder();
        for (TickerSummary tickerSummary : tickerSummaries) {
            summary.append(tickerSummary.toString());
            Optional<Signal> signal = signals.stream().filter(row -> row.getTicker().equals(tickerSummary.getTicker())).findFirst();
            signal.ifPresent(value -> {
                if (value.isBuy()) {
                    summary.append(" Зафиксирован сигнал к покупке.");
                } else {
                    summary.append(" Зафиксирован сигнал к продаже.");
                }
            });
            summary.append("\n");
        }
        return new ScannerLog(
            dateTime,
            summary.toString()
        );
    }
}
